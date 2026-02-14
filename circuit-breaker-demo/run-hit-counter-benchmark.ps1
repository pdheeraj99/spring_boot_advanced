param(
    [int]$ChaosDelaySeconds = 55
)

$ErrorActionPreference = "Stop"

function Wait-ForHealth {
    param(
        [string]$Url,
        [int]$MaxSeconds = 90
    )

    $start = Get-Date
    while (((Get-Date) - $start).TotalSeconds -lt $MaxSeconds) {
        try {
            $response = Invoke-RestMethod -Uri $Url -TimeoutSec 2
            if ($response.status -eq "UP") {
                return $true
            }
        } catch {
        }
        Start-Sleep -Milliseconds 750
    }
    return $false
}

function Get-Hits {
    param(
        [string]$BaseUrl,
        [string]$Key
    )
    return (Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/hit-counter/$Key" -TimeoutSec 10).hits
}

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$resultDir = Join-Path $root "benchmark-results\$timestamp"
$runLogs = Join-Path $resultDir "runlogs"
New-Item -ItemType Directory -Path $runLogs -Force | Out-Null

$jarPath = Join-Path $root "target\circuit-breaker-demo-0.0.1-SNAPSHOT.jar"
$k6ScriptPath = Join-Path $root "src\test\resources\k6\hit-counter-distributed-chaos.js"
$k6SummaryPath = Join-Path $resultDir "k6-summary.json"
$k6StdoutPath = Join-Path $resultDir "k6-output.txt"
$redisSampleCsvPath = Join-Path $resultDir "redis-samples.csv"
$driftSampleCsvPath = Join-Path $resultDir "drift-samples.csv"
$chaosLogPath = Join-Path $resultDir "chaos-events.log"
$metaPath = Join-Path $resultDir "run-meta.json"
$monitorStopFile = Join-Path $resultDir "redis-monitor.stop"

$nodeA = "http://localhost:8080"
$nodeB = "http://localhost:8081"
$counterKey = "orders-benchmark-$([DateTimeOffset]::UtcNow.ToUnixTimeSeconds())"

$appA = $null
$appB = $null
$monitorJob = $null
$driftJob = $null
$chaosJob = $null

try {
    mvnd -DskipTests package | Tee-Object -FilePath (Join-Path $resultDir "build-output.txt")

    docker compose up -d redis | Tee-Object -FilePath (Join-Path $resultDir "docker-up.txt")

    $appA = Start-Process -FilePath "java" -WorkingDirectory $root -ArgumentList @(
        "-jar", $jarPath,
        "--server.port=8080",
        "--app.hit-counter.mode=redis",
        "--logging.level.org.springframework.data.redis=INFO"
    ) -RedirectStandardOutput (Join-Path $runLogs "app-8080.out.log") -RedirectStandardError (Join-Path $runLogs "app-8080.err.log") -PassThru

    $appB = Start-Process -FilePath "java" -WorkingDirectory $root -ArgumentList @(
        "-jar", $jarPath,
        "--server.port=8081",
        "--app.hit-counter.mode=redis",
        "--logging.level.org.springframework.data.redis=INFO"
    ) -RedirectStandardOutput (Join-Path $runLogs "app-8081.out.log") -RedirectStandardError (Join-Path $runLogs "app-8081.err.log") -PassThru

    if (-not (Wait-ForHealth -Url "$nodeA/actuator/health")) {
        throw "Node 8080 did not become healthy."
    }
    if (-not (Wait-ForHealth -Url "$nodeB/actuator/health")) {
        throw "Node 8081 did not become healthy."
    }

    "timestamp_utc,cpu_pct,container_mem,redis_used_memory_bytes,redis_used_memory_human" | Set-Content $redisSampleCsvPath
    "timestamp_utc,node8080_hits,node8081_hits,absolute_drift,error" | Set-Content $driftSampleCsvPath

    $monitorJob = Start-Job -ScriptBlock {
        param($StopFile, $CsvPath)
        while (-not (Test-Path $StopFile)) {
            $ts = (Get-Date).ToUniversalTime().ToString("o")
            $stats = docker stats circuit-demo-redis --no-stream --format "{{.CPUPerc}},{{.MemUsage}}" 2>$null
            $cpu = ""
            $containerMem = ""
            if ($stats) {
                $parts = $stats.Split(",", 2)
                if ($parts.Length -ge 1) { $cpu = $parts[0].Trim() }
                if ($parts.Length -ge 2) { $containerMem = $parts[1].Trim() }
            }

            $usedBytes = ""
            $usedHuman = ""
            $memInfo = docker exec circuit-demo-redis redis-cli INFO memory 2>$null
            foreach ($line in $memInfo) {
                if ($line -like "used_memory:*") {
                    $usedBytes = $line.Split(":", 2)[1].Trim()
                } elseif ($line -like "used_memory_human:*") {
                    $usedHuman = $line.Split(":", 2)[1].Trim()
                }
            }
            "$ts,$cpu,$containerMem,$usedBytes,$usedHuman" | Add-Content $CsvPath
            Start-Sleep -Seconds 1
        }
    } -ArgumentList $monitorStopFile, $redisSampleCsvPath

    $chaosJob = Start-Job -ScriptBlock {
        param($DelaySec, $ChaosLog)
        Start-Sleep -Seconds $DelaySec
        $before = (Get-Date).ToUniversalTime().ToString("o")
        "[$before] restarting redis container" | Add-Content $ChaosLog
        docker restart circuit-demo-redis | Out-Null
        $after = (Get-Date).ToUniversalTime().ToString("o")
        "[$after] redis container restart completed" | Add-Content $ChaosLog
    } -ArgumentList $ChaosDelaySeconds, $chaosLogPath

    $driftJob = Start-Job -ScriptBlock {
        param($StopFile, $DriftCsvPath, $NodeA, $NodeB, $Key)
        while (-not (Test-Path $StopFile)) {
            $ts = (Get-Date).ToUniversalTime().ToString("o")
            try {
                $hitsA = (Invoke-RestMethod -Method Get -Uri "$NodeA/api/hit-counter/$Key" -TimeoutSec 2).hits
                $hitsB = (Invoke-RestMethod -Method Get -Uri "$NodeB/api/hit-counter/$Key" -TimeoutSec 2).hits
                $drift = [math]::Abs([long]$hitsA - [long]$hitsB)
                "$ts,$hitsA,$hitsB,$drift," | Add-Content $DriftCsvPath
            } catch {
                $errorText = $_.Exception.Message -replace ",", ";"
                "$ts,,,,$errorText" | Add-Content $DriftCsvPath
            }
            Start-Sleep -Seconds 2
        }
    } -ArgumentList $monitorStopFile, $driftSampleCsvPath, $nodeA, $nodeB, $counterKey

    $k6Command = "k6 run --summary-export `"$k6SummaryPath`" `"$k6ScriptPath`""
    $env:BASE_URL_A = $nodeA
    $env:BASE_URL_B = $nodeB
    $env:COUNTER_KEY = $counterKey
    Invoke-Expression $k6Command | Tee-Object -FilePath $k6StdoutPath

    New-Item -ItemType File -Path $monitorStopFile -Force | Out-Null
    if ($monitorJob) { Wait-Job $monitorJob | Out-Null }
    if ($driftJob) { Wait-Job $driftJob | Out-Null }
    if ($chaosJob) { Wait-Job $chaosJob | Out-Null }

    $hitsA = Get-Hits -BaseUrl $nodeA -Key $counterKey
    $hitsB = Get-Hits -BaseUrl $nodeB -Key $counterKey
    $drift = [math]::Abs([long]$hitsA - [long]$hitsB)

    [pscustomobject]@{
        timestamp = (Get-Date).ToUniversalTime().ToString("o")
        counterKey = $counterKey
        node8080Hits = $hitsA
        node8081Hits = $hitsB
        absoluteDrift = $drift
        resultDirectory = $resultDir
        chaosDelaySeconds = $ChaosDelaySeconds
    } | ConvertTo-Json | Set-Content $metaPath

    Write-Host "Benchmark run complete. Artifacts: $resultDir"
}
finally {
    if (-not (Test-Path $monitorStopFile)) {
        New-Item -ItemType File -Path $monitorStopFile -Force | Out-Null
    }
    if ($monitorJob) {
        try { Stop-Job $monitorJob -ErrorAction SilentlyContinue | Out-Null } catch {}
    }
    if ($driftJob) {
        try { Stop-Job $driftJob -ErrorAction SilentlyContinue | Out-Null } catch {}
    }
    if ($chaosJob) {
        try { Stop-Job $chaosJob -ErrorAction SilentlyContinue | Out-Null } catch {}
    }
    if ($appA -and -not $appA.HasExited) {
        Stop-Process -Id $appA.Id -Force -ErrorAction SilentlyContinue
    }
    if ($appB -and -not $appB.HasExited) {
        Stop-Process -Id $appB.Id -Force -ErrorAction SilentlyContinue
    }
    docker compose down | Out-Null
}
