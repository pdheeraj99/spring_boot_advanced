$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$executionLog = Join-Path $projectRoot "execution.log"
$devtoolsLog = Join-Path $projectRoot "devtools-restart.log"
$appLog = Join-Path $projectRoot "app-run.log"
$appErrLog = Join-Path $projectRoot "app-run.err.log"

Remove-Item $executionLog, $devtoolsLog, $appLog, $appErrLog -ErrorAction SilentlyContinue

function Write-Step {
    param([string]$Message)
    $ts = Get-Date -Format "yyyy-MM-dd HH:mm:ss.fff"
    $line = "[$ts] $Message"
    $line | Tee-Object -FilePath $executionLog -Append
}

function Wait-Seconds {
    param([int]$Seconds)
    Start-Sleep -Seconds $Seconds
}

function Stop-App {
    if ($script:appProcess -and -not $script:appProcess.HasExited) {
        Stop-Process -Id $script:appProcess.Id -Force
    }
}

function Clear-Port8080 {
    $listeners = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($listeners) {
        $pids = $listeners | Select-Object -ExpandProperty OwningProcess -Unique
        foreach ($procId in $pids) {
            try {
                Stop-Process -Id $procId -Force -ErrorAction Stop
                Write-Step "Stopped existing process on port 8080 (PID=$procId)"
            } catch {
                Write-Step "Unable to stop PID=$procId on port 8080: $($_.Exception.Message)"
            }
        }
    }
}

try {
    Write-Step "STEP 1: Verify mvnd availability"
    & mvnd --version | Tee-Object -FilePath $executionLog -Append | Out-Null

    Write-Step "STEP 1: Build and verify with mvnd clean install"
    & mvnd clean install | Tee-Object -FilePath $executionLog -Append | Out-Null

    Write-Step "STEP 2: Start application with mvnd spring-boot:run"
    Clear-Port8080
    $script:appProcess = Start-Process -FilePath "mvnd" -ArgumentList "spring-boot:run" -WorkingDirectory $projectRoot -RedirectStandardOutput $appLog -RedirectStandardError $appErrLog -PassThru

    Write-Step "Waiting for startup completion"
    $startupDeadline = (Get-Date).AddMinutes(3)
    while ((Get-Date) -lt $startupDeadline) {
        if (Test-Path $appLog) {
            $content = Get-Content $appLog -Raw
            if ($content -match "Started Nplus1DemoApplication") {
                break
            }
        }
        Start-Sleep -Seconds 2
    }

    if (-not ((Get-Content $appLog -Raw) -match "Started Nplus1DemoApplication")) {
        throw "Application startup timeout."
    }

    Write-Step "Startup complete; waiting 10 seconds for initialization"
    Wait-Seconds 10

    Write-Step "STEP 3: DevTools verification by changing controller comment"
    $controllerPath = Join-Path $projectRoot "src/main/java/com/example/nplus1demo/controller/OrderControllerV1.java"
    (Get-Content $controllerPath) -replace "devtools-check-comment.*", ("devtools-check-comment " + (Get-Date -Format "yyyyMMddHHmmss")) | Set-Content $controllerPath

    Write-Step "Waiting 12 seconds for devtools restart"
    Wait-Seconds 12

    $restartDetected = $false
    $pollDeadline = (Get-Date).AddSeconds(10)
    while ((Get-Date) -lt $pollDeadline) {
        $content = Get-Content $appLog -Raw
        if ($content -match "Restarting|restartedMain|Restart classloader") {
            $restartDetected = $true
            break
        }
        Start-Sleep -Seconds 2
    }

    if (-not $restartDetected) {
        throw "DevTools restart not detected."
    }

    Select-String -Path $appLog -Pattern "Restarting|restartedMain|Restart classloader" |
        ForEach-Object { "[{0}] {1}" -f (Get-Date -Format "yyyy-MM-dd HH:mm:ss.fff"), $_.Line } |
        Tee-Object -FilePath $devtoolsLog -Append | Out-Null

    Write-Step "DevTools restart confirmed"

    Write-Step "STEP 4: Warm up /actuator/health and wait 5 seconds"
    Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get | Out-Null
    Wait-Seconds 5

    Write-Step "STEP 5: Test N+1 endpoint"
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/orders/n-plus-one" -Method Get | Out-Null
    Wait-Seconds 1
    $v1Metrics = Invoke-RestMethod -Uri "http://localhost:8080/api/metrics/recent?path=/api/v1/orders/n-plus-one&limit=1" -Method Get
    $v1Queries = [int]$v1Metrics[0].sqlCount
    Write-Step "N+1 latest query count: $v1Queries"
    if ($v1Queries -lt 15) {
        Write-Step "WARNING: V1 query count below 15"
    }

    Write-Step "STEP 6: Test optimized endpoint"
    Invoke-RestMethod -Uri "http://localhost:8080/api/v2/orders/optimized" -Method Get | Out-Null
    Wait-Seconds 1
    $v2Metrics = Invoke-RestMethod -Uri "http://localhost:8080/api/metrics/recent?path=/api/v2/orders/optimized&limit=1" -Method Get
    $v2Queries = [int]$v2Metrics[0].sqlCount
    Write-Step "Optimized latest query count: $v2Queries"
    if ($v2Queries -gt 3) {
        Write-Step "WARNING: V2 query count above 3"
    }

    Write-Step "STEP 7: Run load test endpoint"
    $loadReport = Invoke-RestMethod -Uri "http://localhost:8080/api/test/run-load-test" -Method Post
    Wait-Seconds 1

    Write-Step "STEP 8: Reports generated at test-results.json and test-results.md"

    $banner = @"
===============================================
✓ N+1 QUERY OPTIMIZATION TESTING COMPLETED
===============================================
"@
    $banner | Tee-Object -FilePath $executionLog -Append

    $summary = @(
        "Query Reduction: $($loadReport.queryReductionPercent)% (Target >=80%) => $(if($loadReport.queryReductionPass){'PASS'}else{'FAIL'})",
        "Execution Improvement: $($loadReport.executionImprovementPercent)% (Target >=73%) => $(if($loadReport.executionImprovementPass){'PASS'}else{'FAIL'})",
        "Connection Wait Reduction: $($loadReport.connectionWaitReductionPercent)% (Target >=98%) => $(if($loadReport.connectionWaitReductionPass){'PASS'}else{'FAIL'})",
        "Reports: $projectRoot\test-results.json, $projectRoot\test-results.md",
        "Logs: $executionLog, $devtoolsLog, $appLog, $appErrLog"
    )
    $summary | Tee-Object -FilePath $executionLog -Append
}
catch {
    Write-Step "ERROR: $($_.Exception.Message)"
    throw
}
finally {
    Stop-App
}
