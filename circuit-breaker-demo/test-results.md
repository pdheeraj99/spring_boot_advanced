# Circuit Breaker Resilience Test Report

Generated At: 2026-02-06T15:26:51.2286682+05:30

## Summary Table
| Metric | Value | Target | Status |
|---|---:|---:|---|
| Availability with CB (%) | 100 | >= 99 | PASS |
| Fallback speed improvement (%) | 97.77 | >= 95 | PASS |
| Recovery time (sec) | 14.02 | <= 30 | PASS |
| Redis fallback avg (ms) | 5.04 | < 10 | PASS |
| Circuit opened during failures | True | true | PASS |

## Circuit-Breaker Behavior
- States observed: CLOSED, OPEN, HALF_OPEN
- Total transitions: 10
- Current state: CLOSED

## Response-Time Comparison
- External service avg/p95/p99: 225.85 / 234 / 234 ms
- Redis fallback avg/p95/p99: 5.04 / 7 / 7 ms
- Improvement: 97.77%

## Availability Analysis
- Total calls: 38
- Successful calls: 38
- Failed calls: 0
- Fallback calls: 25
- Availability with circuit breaker: 100%
- Estimated availability without fallback: 34.21%

## State Transitions Timeline
~~~
2026-02-06T09:53:04.170844800Z State transition from CLOSED to OPEN
2026-02-06T09:53:14.172648800Z State transition from OPEN to HALF_OPEN
2026-02-06T09:53:45.541883700Z State transition from HALF_OPEN to OPEN
2026-02-06T09:53:55.548730500Z State transition from OPEN to HALF_OPEN
2026-02-06T09:54:12.467304600Z State transition from HALF_OPEN to CLOSED
2026-02-06T09:55:40.365822600Z State transition from CLOSED to OPEN
2026-02-06T09:55:50.381849100Z State transition from OPEN to HALF_OPEN
2026-02-06T09:56:21.729637900Z State transition from HALF_OPEN to OPEN
2026-02-06T09:56:31.738951800Z State transition from OPEN to HALF_OPEN
2026-02-06T09:56:49.581768800Z State transition from HALF_OPEN to CLOSED
~~~

## Docker/Redis Status
- Container: circuit-demo-redis
- Redis keys snapshot from setup:
~~~
product:2 product:4 product:1 product:3 product:5
~~~
- Redis port mapping: 6379:6379
