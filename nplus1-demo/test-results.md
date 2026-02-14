# N+1 Optimization Test Results
Generated: 2026-02-06T14:05:24.840771500

| Metric | V1 N+1 | V2 Optimized | Improvement | Target | Status |
|---|---:|---:|---:|---:|---|
| Avg Queries/Request | 94.00 | 2.00 | 97.87% | >=80% | PASS |
| Avg Response Time (ms) | 749.86 | 142.06 | 81.05% | >=73% | PASS |
| Avg Connection Wait (ms) | 1302033.48 | 4495.32 | 99.65% | >=98% | PASS |

## Latency Percentiles
- V1 P95: 1129.39 ms
- V1 P99: 0.00 ms
- V2 P95: 215.07 ms
- V2 P99: 0.00 ms

## Hibernate Statistics
```json
{
  "queryExecutionCount" : 9,
  "prepareStatementCount" : 5,
  "connectCount" : 0
}
```
