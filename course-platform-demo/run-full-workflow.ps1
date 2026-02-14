$ErrorActionPreference = "Stop"
$script:appProcess = $null

function Stop-App {
    if ($script:appProcess -and -not $script:appProcess.HasExited) {
        Stop-Process -Id $script:appProcess.Id -Force
        Start-Sleep -Seconds 2
    }
}

try {
    $mavenCmd = "..\\nplus1-demo\\mvnw.cmd"
    Remove-Item -ErrorAction SilentlyContinue execution.log, test-results.json, test-results.md

    "[$(Get-Date -Format o)] Starting workflow" | Tee-Object -FilePath execution.log -Append

    & $mavenCmd -f pom.xml -q clean test
    if ($LASTEXITCODE -ne 0) { throw "mvn clean test failed" }

    "[$(Get-Date -Format o)] Starting Spring Boot app" | Tee-Object -FilePath execution.log -Append
    $script:appProcess = Start-Process -FilePath $mavenCmd -ArgumentList "-f", "pom.xml", "spring-boot:run" -PassThru -NoNewWindow -RedirectStandardOutput app-run.log -RedirectStandardError app-run.err.log

    $healthUrl = "http://localhost:8080/actuator/health"
    $maxRetries = 60
    $ready = $false
    for ($i = 0; $i -lt $maxRetries; $i++) {
        try {
            $health = Invoke-RestMethod -Uri $healthUrl -Method Get -TimeoutSec 2
            if ($health.status -eq "UP") {
                $ready = $true
                break
            }
        }
        catch {
            Start-Sleep -Seconds 2
        }
    }

    if (-not $ready) { throw "Application did not become healthy" }

    "[$(Get-Date -Format o)] Running smoke endpoint" | Tee-Object -FilePath execution.log -Append
    $smoke = Invoke-RestMethod -Uri "http://localhost:8080/api/test/run-smoke-suite" -Method Post
    $courseCompare = Invoke-RestMethod -Uri "http://localhost:8080/api/test/compare-courses" -Method Get
    $dashboardCompare = Invoke-RestMethod -Uri "http://localhost:8080/api/test/compare-dashboard" -Method Get
    $latest = Invoke-RestMethod -Uri "http://localhost:8080/api/metrics/latest" -Method Get

    $result = [PSCustomObject]@{
        executedAt = (Get-Date -Format o)
        smokeSuccess = $smoke.success
        smokeMessage = $smoke.message
        smokeQueryComparison = $smoke.queryComparison
        courseComparison = $courseCompare
        dashboardComparison = $dashboardCompare
        latestMetric = $latest
    }

    $result | ConvertTo-Json -Depth 8 | Set-Content test-results.json

    @"
# Course Platform Demo - Test Results

- Executed At: $($result.executedAt)
- Smoke Success: $($result.smokeSuccess)
- Smoke Message: $($result.smokeMessage)

## Query Comparison (Course V1 vs V2)
- V1 Queries: $($result.courseComparison.v1Queries)
- V2 Queries: $($result.courseComparison.v2Queries)
- Reduction: $([math]::Round($result.courseComparison.reductionPercent, 2))%

## Query Comparison (Dashboard V1 vs V2)
- V1 Queries: $($result.dashboardComparison.v1Queries)
- V2 Queries: $($result.dashboardComparison.v2Queries)
- Reduction: $([math]::Round($result.dashboardComparison.reductionPercent, 2))%

## Latest Metric Endpoint
- V1 Queries: $($result.latestMetric.v1Queries)
- V2 Queries: $($result.latestMetric.v2Queries)
- Reduction: $([math]::Round($result.latestMetric.reductionPercent, 2))%
"@ | Set-Content test-results.md

    "[$(Get-Date -Format o)] Workflow completed successfully" | Tee-Object -FilePath execution.log -Append
}
catch {
    "[$(Get-Date -Format o)] Workflow failed: $($_.Exception.Message)" | Tee-Object -FilePath execution.log -Append
    throw
}
finally {
    Stop-App
}
