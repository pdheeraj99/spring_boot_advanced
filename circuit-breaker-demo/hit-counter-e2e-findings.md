# Hit Counter E2E Findings

Date: 2026-02-11  
Project: `d:\spring_boot_advanced_demos\circuit-breaker-demo`  
Mode under test: `app.hit-counter.mode=redis`  
Build tool used: `mvnd` (not `mvn`)

## Test Setup

1. Started Redis via Docker Compose (`redis:7.2-alpine`).
2. Built jar with `mvnd -DskipTests package`.
3. Ran 2 app instances in distributed mode:
   - Node A: `--server.port=8080 --app.hit-counter.mode=redis`
   - Node B: `--server.port=8081 --app.hit-counter.mode=redis`
4. Verified health for both nodes through `/actuator/health`.

## Scenario 1: Distributed Consistency Under Concurrent Writes

Workload:
- 40 parallel writers total (20 targeting `8080`, 20 targeting `8081`)
- 150 POST hits per writer
- Expected total writes: `6000`
- Endpoint: `POST /api/hit-counter/{key}/hit`

Observed output:

```json
{"key":"orders-e2e-1770824846","expectedWrites":6000,"successfulWrites":6000,"droppedWrites":0,"writeDurationMs":98882.08,"estimatedWriteThroughputPerSec":60.68,"node8080HitsInitial":6000,"node8081HitsInitial":6000,"node8080HitsAfter2s":6000,"node8081HitsAfter2s":6000,"mode8080":"redis","mode8081":"redis","windowSeconds":300,"avgReadLatencyMs":4.54,"p95ReadLatencyMs":5.2}
```

Findings:
- Distributed correctness passed: both nodes returned `6000`.
- Write loss was `0` for this run.
- Read-after-write consistency across nodes looked stable (same counts immediately and after 2 seconds).

## Scenario 2: Window Expiry Behavior (Short Window)

Setup:
- Started a third node:
  - Node C: `--server.port=8082 --app.hit-counter.mode=redis --app.hit-counter.window-seconds=5`
- Wrote 50 hits to a single key on `8082`.

Observed output:

```json
{"key":"orders-expiry-1770824993","immediate":50,"after3Seconds":50,"after6Seconds":0,"configuredWindowSeconds":5}
```

Findings:
- Window behavior passed:
  - within 5 seconds count remained visible,
  - after 6 seconds count dropped to 0.

## Redis State and Footprint

Observed:
- `used_memory_human:1.09M`
- `DBSIZE: 1`
- key count for pattern `hit-counter:*`: `1`
- `EXISTS hit-counter:orders-expiry-1770824993` returned `0` after expiry.
- `HLEN hit-counter:orders-e2e-1770824846` returned `37` (seconds/buckets retained in hash for this key at sampling time).

Interpretation:
- Storage remains compact and time-bucketed.
- Expired short-window key was removed as expected.

## Parallel “Second Brain” Observations

1. **Correctness looked strong for distributed reads/writes** in this run (no drift observed between nodes).
2. **Measured write throughput (`~60 req/s`) is not service ceiling**:
   - This test uses PowerShell background jobs + `Invoke-RestMethod`, which is slow and heavy.
   - It is useful for correctness checks, not realistic max-QPS benchmarking.
3. **Logging overhead is high in current config**:
   - `org.springframework.data.redis: DEBUG` produced heavy connection logs.
   - This can materially reduce throughput and add noise in load tests.
4. **Model behavior is bucketized by second**:
   - Redis hash field count (`HLEN`) tracks active second-buckets, not total hits.

## What This Proves

- The implemented design is achievable and functional end-to-end in microservice mode.
- Multiple service instances share a single logical counter through Redis.
- Time-window expiration semantics are working in runtime, not just unit tests.

## Gaps Before “Serious” Performance Claims

1. Need a real load generator (`k6`, `wrk`, or Gatling) instead of PowerShell loops.
2. Need logs reduced to `INFO` for fair throughput measurement.
3. Need multi-minute soak tests for stability and Redis memory growth trends.
4. Need failure-mode validation (Redis restart, one-node crash, network jitter).

## Recommended Next Benchmark Pass

1. Set `org.springframework.data.redis` logging to `INFO`.
2. Run `k6` script with staged ramp (for example 100 -> 1k -> 5k VUs equivalent RPS goals).
3. Capture:
   - write success/failure ratio,
   - p50/p95/p99 latency,
   - counter drift across nodes,
   - Redis memory/CPU over time.
4. Add one chaos scenario: restart Redis container during load and measure recovery behavior.
