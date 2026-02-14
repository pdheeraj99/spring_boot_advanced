param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ProjectRoot = "d:\docker_and_k8s\circuit-breaker-demo"
)

$ErrorActionPreference = "Stop"

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path
    )
    $uri = "$BaseUrl$Path"
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    try {
        $response = Invoke-RestMethod -Method $Method -Uri $uri -TimeoutSec 15
        $sw.Stop()
        return [pscustomobject]@{
            ok = $true
            data = $response
            elapsedMs = [int]$sw.ElapsedMilliseconds
            error = $null
        }
    } catch {
        $sw.Stop()
        return [pscustomobject]@{
            ok = $false
            data = $null
            elapsedMs = [int]$sw.ElapsedMilliseconds
            error = $_.Exception.Message
        }
    }
}

function Get-CircuitState {
    $cb = Invoke-Api -Method GET -Path "/actuator/circuitbreakers"
    if ($cb.ok -and $cb.data.circuitBreakers.externalService.state) {
        return [string]$cb.data.circuitBreakers.externalService.state
    }
    return "UNKNOWN"
}

function Percentile {
    param([double[]]$Values, [int]$P)
    if (-not $Values -or $Values.Count -eq 0) { return 0 }
    $sorted = $Values | Sort-Object
    $idx = [math]::Ceiling(($P / 100.0) * $sorted.Count) - 1
    if ($idx -lt 0) { $idx = 0 }
    if ($idx -ge $sorted.Count) { $idx = $sorted.Count - 1 }
    return [math]::Round($sorted[$idx], 2)
}

function Avg {
    param([double[]]$Values)
    if (-not $Values -or $Values.Count -eq 0) { return 0 }
    return [math]::Round((($Values | Measure-Object -Average).Average), 2)
}

$calls = New-Object System.Collections.Generic.List[object]

function Record-Call {
    param(
        [string]$Phase,
        [object]$CallResult
    )
    if ($CallResult.ok -and $CallResult.data) {
        $source = [string]$CallResult.data.source
        $respMs = if ($CallResult.data.responseTimeMs -ne $null) { [double]$CallResult.data.responseTimeMs } else { [double]$CallResult.elapsedMs }
        $state = [string]$CallResult.data.circuitState
        $message = [string]$CallResult.data.message
        $isSuccess = $source -ne "FALLBACK_ERROR"
    } else {
        $source = "FALLBACK_ERROR"
        $respMs = [double]$CallResult.elapsedMs
        $state = "UNKNOWN"
        $message = [string]$CallResult.error
        $isSuccess = $false
    }

    $calls.Add([pscustomobject]@{
        phase = $Phase
        source = $source
        responseTimeMs = $respMs
        circuitState = $state
        message = $message
        success = $isSuccess
    }) | Out-Null
}

# Phase 1 setup
$redisRunning = docker ps --filter "name=circuit-demo-redis" --format "{{.Names}}"
if (-not ($redisRunning -contains "circuit-demo-redis")) {
    docker compose -f (Join-Path $ProjectRoot "docker-compose.yml") up -d | Out-Null
    Start-Sleep -Seconds 5
}

$seed = Invoke-Api -Method POST -Path "/api/cache/seed"
if (-not $seed.ok) {
    throw "Failed to seed cache: $($seed.error)"
}
$redisKeys = docker exec circuit-demo-redis redis-cli KEYS "*"

Start-Sleep -Seconds 5

# Phase 2 baseline
$null = Invoke-Api -Method POST -Path "/external-api/control/reset"
Start-Sleep -Seconds 3
for ($i=0; $i -lt 10; $i++) {
    $r = Invoke-Api -Method GET -Path "/api/products/1"
    Record-Call -Phase "baseline" -CallResult $r
    Start-Sleep -Seconds 1
}

Start-Sleep -Seconds 5

# Phase 3 failure
$null = Invoke-Api -Method POST -Path "/external-api/control/fail"
Start-Sleep -Seconds 3
for ($i=0; $i -lt 10; $i++) {
    $r = Invoke-Api -Method GET -Path "/api/products/1"
    Record-Call -Phase "failure" -CallResult $r
    Start-Sleep -Seconds 1
}
for ($i=0; $i -lt 5; $i++) {
    $r = Invoke-Api -Method GET -Path "/api/products/1"
    Record-Call -Phase "failure-open" -CallResult $r
    Start-Sleep -Seconds 1
}

Start-Sleep -Seconds 5

# Phase 4 slow
$null = Invoke-Api -Method POST -Path "/external-api/control/reset"
Start-Sleep -Seconds 15
$null = Invoke-Api -Method POST -Path "/external-api/control/slow"
Start-Sleep -Seconds 3
for ($i=0; $i -lt 10; $i++) {
    $r = Invoke-Api -Method GET -Path "/api/products/1"
    Record-Call -Phase "slow" -CallResult $r
    Start-Sleep -Seconds 1
}

Start-Sleep -Seconds 5

# Phase 5 recovery
$null = Invoke-Api -Method POST -Path "/external-api/control/reset"
$recoveryStart = Get-Date
Start-Sleep -Seconds 10
$recoveryClosedAt = $null
for ($i=0; $i -lt 3; $i++) {
    $r = Invoke-Api -Method GET -Path "/api/products/1"
    Record-Call -Phase "recovery" -CallResult $r
    Start-Sleep -Milliseconds 400
    $stateNow = Get-CircuitState
    if ($stateNow -eq "CLOSED" -and -not $recoveryClosedAt) {
        $recoveryClosedAt = Get-Date
    }
    Start-Sleep -Seconds 1
}

# Phase 6 metrics
$metricsResult = Invoke-Api -Method GET -Path "/api/metrics/circuit-breaker"
if (-not $metricsResult.ok) {
    throw "Failed to fetch metrics endpoint: $($metricsResult.error)"
}
$metrics = $metricsResult.data

$totalCalls = $calls.Count
$successfulCalls = ($calls | Where-Object { $_.success }).Count
$fallbackCalls = ($calls | Where-Object { $_.source -in @("REDIS_CACHE", "FALLBACK_ERROR") }).Count
$failedCalls = $totalCalls - $successfulCalls

$fallbackPct = if ($totalCalls -gt 0) { [math]::Round(($fallbackCalls * 100.0) / $totalCalls, 2) } else { 0 }
$availabilityWith = if ($totalCalls -gt 0) { [math]::Round(($successfulCalls * 100.0) / $totalCalls, 2) } else { 0 }
$availabilityWithout = if ($totalCalls -gt 0) { [math]::Round(((($calls | Where-Object { $_.source -eq "EXTERNAL_SERVICE" }).Count * 100.0) / $totalCalls), 2) } else { 0 }

$externalTimes = @($calls | Where-Object { $_.source -eq "EXTERNAL_SERVICE" } | ForEach-Object { [double]$_.responseTimeMs })
$fallbackTimes = @($calls | Where-Object { $_.source -eq "REDIS_CACHE" } | ForEach-Object { [double]$_.responseTimeMs })

$externalAvg = Avg $externalTimes
$externalP95 = Percentile $externalTimes 95
$externalP99 = Percentile $externalTimes 99
$fallbackAvg = Avg $fallbackTimes
$fallbackP95 = Percentile $fallbackTimes 95
$fallbackP99 = Percentile $fallbackTimes 99
$improvement = if ($externalAvg -gt 0) { [math]::Round((($externalAvg - $fallbackAvg) * 100.0) / $externalAvg, 2) } else { 0 }

$recoverySec = if ($recoveryClosedAt) { [math]::Round((New-TimeSpan -Start $recoveryStart -End $recoveryClosedAt).TotalSeconds, 2) } else { 9999 }

$statesSeen = @($calls | Select-Object -ExpandProperty circuitState -Unique)
$circuitOpened = $statesSeen -contains "OPEN"
$fallbackLt10 = ($fallbackAvg -lt 10)

$availabilityPass = $availabilityWith -ge 99.0
$fallbackSpeedPass = $improvement -ge 95.0
$recoveryPass = $recoverySec -le 30

$results = [ordered]@{
    generatedAt = (Get-Date).ToString("o")
    summary = [ordered]@{
        totalCalls = $totalCalls
        successfulCalls = $successfulCalls
        failedCalls = $failedCalls
        fallbackCalls = $fallbackCalls
        fallbackPercentage = $fallbackPct
        availabilityWithCircuitBreaker = $availabilityWith
        availabilityWithoutCircuitBreaker = $availabilityWithout
    }
    responseTimes = [ordered]@{
        externalService = [ordered]@{ avgMs = $externalAvg; p95Ms = $externalP95; p99Ms = $externalP99 }
        redisFallback = [ordered]@{ avgMs = $fallbackAvg; p95Ms = $fallbackP95; p99Ms = $fallbackP99 }
        improvementPercent = $improvement
    }
    circuitStates = [ordered]@{
        closedDurationSec = [int64]($metrics.durations.closedDurationSec)
        openDurationSec = [int64]($metrics.durations.openDurationSec)
        halfOpenDurationSec = [int64]($metrics.durations.halfOpenDurationSec)
        totalTransitions = [int64]($metrics.totalTransitions)
    }
    targets = [ordered]@{
        availabilityTarget = 99.0
        availabilityPass = $availabilityPass
        fallbackSpeedTarget = 95.0
        fallbackSpeedPass = $fallbackSpeedPass
        recoveryTimeTarget = 30
        recoveryTimePass = $recoveryPass
    }
}

$jsonPath = Join-Path $ProjectRoot "test-results.json"
$mdPath = Join-Path $ProjectRoot "test-results.md"

$results | ConvertTo-Json -Depth 10 | Set-Content -Encoding utf8 $jsonPath

$timeline = @($metrics.timeline)
$timelineText = if ($timeline.Count -gt 0) { ($timeline -join "`n") } else { "No transitions recorded" }

$md = @"
# Circuit Breaker Resilience Test Report

Generated At: $($results.generatedAt)

## Summary Table
| Metric | Value | Target | Status |
|---|---:|---:|---|
| Availability with CB (%) | $availabilityWith | >= 99 | $(if($availabilityPass){'PASS'}else{'FAIL'}) |
| Fallback speed improvement (%) | $improvement | >= 95 | $(if($fallbackSpeedPass){'PASS'}else{'FAIL'}) |
| Recovery time (sec) | $recoverySec | <= 30 | $(if($recoveryPass){'PASS'}else{'FAIL'}) |
| Redis fallback avg (ms) | $fallbackAvg | < 10 | $(if($fallbackLt10){'PASS'}else{'FAIL'}) |
| Circuit opened during failures | $circuitOpened | true | $(if($circuitOpened){'PASS'}else{'FAIL'}) |

## Circuit-Breaker Behavior
- States observed: $($statesSeen -join ', ')
- Total transitions: $($metrics.totalTransitions)
- Current state: $($metrics.currentState)

## Response-Time Comparison
- External service avg/p95/p99: $externalAvg / $externalP95 / $externalP99 ms
- Redis fallback avg/p95/p99: $fallbackAvg / $fallbackP95 / $fallbackP99 ms
- Improvement: $improvement%

## Availability Analysis
- Total calls: $totalCalls
- Successful calls: $successfulCalls
- Failed calls: $failedCalls
- Fallback calls: $fallbackCalls
- Availability with circuit breaker: $availabilityWith%
- Estimated availability without fallback: $availabilityWithout%

## State Transitions Timeline
~~~
$timelineText
~~~

## Docker/Redis Status
- Container: circuit-demo-redis
- Redis keys snapshot from setup:
~~~
$redisKeys
~~~
- Redis port mapping: 6379:6379
"@

$md | Set-Content -Encoding utf8 $mdPath

Write-Output "Results written: $jsonPath"
Write-Output "Results written: $mdPath"
