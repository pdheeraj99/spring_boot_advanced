# Hit Counter Recommended Benchmark Pass

Date: 2026-02-11  
Run ID: `20260211-213302`  
Artifacts: `circuit-breaker-demo/benchmark-results/20260211-213302`

## What Was Executed

1. Redis logging reduced to INFO at runtime using:
   - `--logging.level.org.springframework.data.redis=INFO`
2. Staged `k6` ramp executed using:
   - `circuit-breaker-demo/src/test/resources/k6/hit-counter-distributed-chaos.js`
   - stages: `100 -> 1000 -> 5000 -> 1000 -> 0` iterations/sec
3. Two microservice nodes ran in distributed mode:
   - `8080` and `8081` with `--app.hit-counter.mode=redis`
4. Chaos injected during load:
   - Redis container restarted at 55s into run
5. Additional runtime sampling collected:
   - `drift-samples.csv` (cross-node counter drift timeline)
   - `redis-samples.csv` (Redis CPU/memory over time)

## Requested Metrics

### 1) Write success/failure ratio

From `k6-summary.json`:
- `writes_attempted`: `65055`
- `writes_succeeded`: `51417`
- `writes_failed`: `13638`
- write success ratio: `79.04%`
- write failure ratio: `20.96%`

### 2) Latency p50/p95/p99

From `k6-output.txt` (`http_req_duration`):
- p50: `77.93 ms`
- p95: `1.55 s`
- p99: `5.13 s`

Also observed:
- avg: `395.32 ms`
- max: `39.3 s`

### 3) Counter drift across nodes

Final state from `run-meta.json`:
- node `8080`: `51632`
- node `8081`: `51632`
- final absolute drift: `0`

Timeline from `drift-samples.csv`:
- total drift samples: `34`
- valid samples: `28`
- samples with timeout error: `6`
- max observed drift: `339` (early warm-up transient)
- avg observed drift: `39.32`
- post-chaos valid samples:
  - max drift: `20`
  - avg drift: `8.6`
  - eventually converged to `0`

### 4) Redis memory/CPU over time

From `redis-samples.csv`:
- sample count: `23`
- CPU avg: `24.21%`
- CPU max: `94.54%`
- Redis used memory min: `1.01 MB`
- Redis used memory max: `1.11 MB`

## Chaos Scenario Findings (Redis Restart During Load)

From `chaos-events.log`:
- restart initiated: `2026-02-11T16:05:29.1734268Z`
- restart completed: `2026-02-11T16:05:32.7927715Z`

Recovery observation:
- first drift-sampler timeout at `2026-02-11T16:05:30.8632899Z`
- first successful post-restart drift sample at `2026-02-11T16:06:17.1306293Z`
- observed recovery-to-stable-sample time: `~44.34s`

Runtime behavior observed in app logs:
- reconnect events logged on both nodes after restart.
- high load + chaos produced many Redis timeout/loading exceptions (expected under this stress level), while service eventually recovered and counters converged.

## Additional Signals Worth Noting

1. `k6` dropped iterations were high (`108444`), which indicates injector/app saturation at the 5000 stage on this machine.
2. Thresholds in console output:
   - `http_req_failed rate<0.30`: pass
   - `write_success_rate > 0.70`: pass
   - `http_req_duration p(95)<500ms`: fail
3. Summary-export JSON for some thresholds did not match console threshold verdicts in this `k6` run; console values were used as source of truth for pass/fail labels.

## Files Added for Repeatability

1. `circuit-breaker-demo/src/test/resources/k6/hit-counter-distributed-chaos.js`
2. `circuit-breaker-demo/run-hit-counter-benchmark.ps1`

Run command:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run-hit-counter-benchmark.ps1
```
