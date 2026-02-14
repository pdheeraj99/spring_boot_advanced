# N+1 Demo Notes (Presentation Ready)

## 1) Project Goal
- Main objective: N+1 query problem ni reproduce chesi, optimized version tho compare chesi measurable improvements prove cheyyadam.
- Two endpoints:
  - V1 (intentionally N+1): `/api/v1/orders/n-plus-one`
  - V2 (optimized): `/api/v2/orders/optimized`

## 2) Problem Setup (Why N+1 Happens)
`Order` entity lo child collections lazy ga unnayi. Parent orders fetch chesaka, prati order ki separate child queries fire avuthayi.

Snippet: `src/main/java/com/example/nplus1demo/entity/Order.java`
```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private List<OrderItem> orderItems = new ArrayList<>();

@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private List<OrderAudit> orderAudits = new ArrayList<>();
```

And `OrderItem -> Product` also lazy:

Snippet: `src/main/java/com/example/nplus1demo/entity/OrderItem.java`
```java
@ManyToOne(fetch = FetchType.LAZY)
private Product product;
```

## 3) V1 vs V2 Business Logic
### V1 (N+1 path)
- `findAll()` use chestundi.
- DTO mapping lo lazy fields touch avvadam vallaa multiple SQL queries trigger avuthayi.

Snippet: `src/main/java/com/example/nplus1demo/service/OrderService.java`
```java
@Transactional(readOnly = true)
public List<OrderResponse> getAllOrdersWithNPlusOne() {
    List<Order> orders = orderRepository.findAll();
    return orders.stream().map(this::toDtoWithLazyAccess).toList();
}
```

### V2 (Optimized path)
- `JOIN FETCH` to bulk-fetch orderItems + product.
- Separate batched query for audits.

Snippet: `src/main/java/com/example/nplus1demo/service/OrderService.java`
```java
@Transactional(readOnly = true)
public List<OrderResponse> getAllOrdersOptimized() {
    List<Order> orders = orderRepository.findAllWithJoinFetch();
    Set<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toSet());
    if (!orderIds.isEmpty()) {
        orderRepository.fetchAuditsForOrders(orderIds);
    }
    return orders.stream().map(this::toDto).toList();
}
```

Snippet: `src/main/java/com/example/nplus1demo/repository/OrderRepository.java`
```java
@Query("SELECT DISTINCT o FROM Order o " +
       "LEFT JOIN FETCH o.orderItems oi " +
       "LEFT JOIN FETCH oi.product")
List<Order> findAllWithJoinFetch();

@Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderAudits WHERE o.id IN :orderIds")
List<Order> fetchAuditsForOrders(@Param("orderIds") Set<Long> orderIds);
```

## 4) How We Measured SQL Count per Request
- Request filter prati request ki `requestId` create chesi MDC lo store chesindi.
- Hibernate `StatementInspector` SQL execute ayina prathi sari request-level counter increment chesindi.

Snippet: `src/main/java/com/example/nplus1demo/metrics/RequestMetricsFilter.java`
```java
String requestId = UUID.randomUUID().toString();
MDC.put("requestId", requestId);
store.start(requestId, request.getRequestURI());
...
store.finish(requestId, response.getStatus());
```

Snippet: `src/main/java/com/example/nplus1demo/metrics/SqlCountingStatementInspector.java`
```java
public String inspect(String sql) {
    String requestId = MDC.get("requestId");
    if (requestId != null && sql != null && !sql.isBlank()) {
        RequestMetricsStore.getInstance().incrementSql(requestId);
    }
    return sql;
}
```

## 5) Load Test Automation
- k6 script dwara 50 VUs, 50 iterations to endpoint stress test chesam.

Snippet: `src/test/resources/k6/nplus1.js`
```javascript
export const options = {
  vus,
  iterations,
  thresholds: {
    http_req_failed: ['rate<0.1'],
  },
};
```

- Java service rendu scenarios run chesi report object build chesindi.

Snippet: `src/main/java/com/example/nplus1demo/service/LoadTestService.java`
```java
LoadTestScenarioResult nplusOne = runSingleScenario("/api/v1/orders/n-plus-one", Path.of("k6-v1-summary.json"));
LoadTestScenarioResult optimized = runSingleScenario("/api/v2/orders/optimized", Path.of("k6-v2-summary.json"));

double queryReduction = percentageReduction(nplusOne.avgQueriesPerRequest(), optimized.avgQueriesPerRequest());
```

## 6) End-to-End Workflow Execution
- Full workflow script build + app start + devtools verify + single-call check + load-test + report generation automate chesindi.

Snippet: `run-full-workflow.ps1`
```powershell
& mvnd clean install
$script:appProcess = Start-Process -FilePath "mvnd" -ArgumentList "spring-boot:run" ...
...
Invoke-RestMethod -Uri "http://localhost:8080/api/test/run-load-test" -Method Post
```

DevTools proof:
- `devtools-restart.log` lo restart evidence capture chesam.
- `execution.log` lo `DevTools restart confirmed` line undi.

## 7) What We Achieved (Measured Results)
Source: `test-results.md`, `test-results.json`

- Avg Queries/Request:
  - V1: 94
  - V2: 2
  - Improvement: **97.87%** (PASS)

- Avg Response Time:
  - V1: 749.86 ms
  - V2: 142.06 ms
  - Improvement: **81.05%** (PASS)

- Avg Connection Wait:
  - V1: 1,302,033.48 ms
  - V2: 4,495.32 ms
  - Improvement: **99.65%** (PASS)

- Direct execution proof from log (`execution.log`):
  - `N+1 latest query count: 94`
  - `Optimized latest query count: 2`

## 8) Final One-Line Explanation (for interviews/presentation)
"Manam same business response kosam V1 lo lazy-loading vallaa N+1 queries produce chesi, V2 lo JOIN FETCH + batch fetch strategy implement chesi, request-level SQL instrumentation + k6 load test tho 97.87% query reduction, 81.05% latency improvement, 99.65% connection-wait reduction ni measurable ga prove chesam."

## 9) Artifacts You Can Show
- `execution.log`
- `devtools-restart.log`
- `k6-v1-summary.json`
- `k6-v2-summary.json`
- `test-results.json`
- `test-results.md`
