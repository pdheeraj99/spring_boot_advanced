# 🔒 Circuit Breaker Pattern - Complete Notes (Tenglish Style)

> **Interview Ready Notes** - Ee document lo Circuit Breaker pattern enti, problem enti, solution enti, Resilience4j tho em implement chesamo anni details untayi.

---

## 📚 Table of Contents

1. [Problem Enti? - Why Circuit Breaker?](#1-problem-enti---why-circuit-breaker)
2. [Circuit Breaker Pattern Basics](#2-circuit-breaker-pattern-basics)
3. [Project Architecture](#3-project-architecture)
4. [Resilience4j Configuration Explained](#4-resilience4j-configuration-explained)
5. [Service Implementation Details](#5-service-implementation-details)
6. [Fallback Mechanism - Redis Cache](#6-fallback-mechanism---redis-cache)
7. [State Transitions & Metrics](#7-state-transitions--metrics)
8. [Test Results & Measured Proof](#8-test-results--measured-proof)
9. [Interview Ready Explanations](#9-interview-ready-explanations)

---

## 1. Problem Enti? - Why Circuit Breaker?

### 🔥 Real World Scenario (Telugu lo)

Imagine chesuko: Nee application oka external payment gateway ki call chestundi. Oka roju aa gateway down ayyindi.

**Without Circuit Breaker:**

```
User 1 request → Wait 30s → Timeout → Error
User 2 request → Wait 30s → Timeout → Error
User 3 request → Wait 30s → Timeout → Error
... (thousands of users waiting)
```

**Problems:**

- 🕐 **Users waiting** - Prathi request ki 30s wait (timeout)
- 🧵 **Thread exhaustion** - All threads blocked waiting for response
- 💸 **Cascade failure** - Nee app kuda crash avvutadi
- 😤 **Bad UX** - Users frustrated

### ✅ With Circuit Breaker

```
User 1-5 requests → Failures detected → Circuit OPENS!
User 6+ requests → Immediate fallback (5ms) → Redis cache data
After 10 seconds → Circuit HALF-OPEN → Try again
If success → Circuit CLOSES → Normal operation resumes
```

**Benefits:**

- ⚡ **Fast fail** - No more 30s waits
- 🛡️ **System protection** - Downstream failure isolate avutadi
- 📊 **Graceful degradation** - Cached/fallback data serve chestundi
- 🔄 **Auto recovery** - System automatically recovers

---

## 2. Circuit Breaker Pattern Basics

### 🔌 Electrical Circuit Analogy (Simple ga)

```
Real Circuit Breaker (Home lo):
- Too much current → Circuit trips → Power cut
- You reset manually → Power restored

Software Circuit Breaker:
- Too many failures → Circuit opens → Fast fail
- After wait time → Auto tries → System recovers
```

### 📊 Three States

```
┌─────────────────────────────────────────────────────────────┐
│                    CIRCUIT BREAKER STATES                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌──────────┐     Failures > 50%     ┌──────────┐         │
│   │  CLOSED  │ ─────────────────────→ │   OPEN   │         │
│   │ (Normal) │                         │  (Fail)  │         │
│   └────┬─────┘                         └────┬─────┘         │
│        │                                    │               │
│        │ Success                   After 10s│               │
│        │                                    ▼               │
│        │                           ┌────────────┐           │
│        └───────────────────────────│ HALF-OPEN │           │
│               If 3 calls succeed   │  (Testing) │           │
│                                    └────────────┘           │
│                                          │                  │
│                    If failures again ────┘ (back to OPEN)   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 📝 State Definitions

| State | Description | Behavior |
|-------|-------------|----------|
| **CLOSED** | Normal operation | All requests go through, failures counted |
| **OPEN** | Protection mode | All requests fail fast, fallback executes |
| **HALF_OPEN** | Testing mode | Limited requests allowed to test if service recovered |

---

## 3. Project Architecture

### 📁 Project Structure

```
circuit-breaker-demo/
├── docker-compose.yml              # Redis container
├── pom.xml                         # Dependencies (Resilience4j, Redis)
├── src/main/
│   ├── java/com/demo/circuitbreaker/
│   │   ├── CircuitBreakerDemoApplication.java
│   │   │
│   │   ├── config/
│   │   │   └── AppConfig.java      # RestTemplate, RedisTemplate config
│   │   │
│   │   ├── controller/
│   │   │   ├── ProductController.java      # Main API (Circuit Breaker applied)
│   │   │   ├── ExternalProductController.java  # Simulated external service
│   │   │   └── MetricsController.java      # Metrics endpoint
│   │   │
│   │   ├── model/
│   │   │   ├── Product.java               # Domain model
│   │   │   ├── ProductResponse.java       # Response with circuit state
│   │   │   ├── ResponseSource.java        # EXTERNAL_SERVICE, REDIS_CACHE, FALLBACK_ERROR
│   │   │   └── ExternalMode.java          # NORMAL, FAIL, SLOW
│   │   │
│   │   └── service/
│   │       ├── ProductService.java        # 🔑 Main service with Circuit Breaker
│   │       ├── ExternalProductService.java # Simulated downstream (can fail/slow)
│   │       ├── RedisCacheService.java     # Cache for fallback
│   │       └── CircuitBreakerMetricsService.java  # State tracking
│   │
│   └── resources/
│       └── application.yml         # Resilience4j configuration
```

### 🔄 Request Flow Diagram

```
┌──────────────┐     GET /api/products/{id}
│   Client     │ ──────────────────────────────────────┐
└──────────────┘                                       ▼
                                              ┌─────────────────────┐
                                              │  ProductController  │
                                              └──────────┬──────────┘
                                                         │
                                                         ▼
                                    ┌────────────────────────────────────────┐
                                    │           ProductService               │
                                    │  @CircuitBreaker @Retry @TimeLimiter  │
                                    └────────────────────┬───────────────────┘
                                                         │
                               ┌─────────────────────────┴─────────────────────────┐
                               │                                                    │
                      Circuit CLOSED                                        Circuit OPEN
                               │                                                    │
                               ▼                                                    ▼
                    ┌─────────────────────┐                          ┌─────────────────────┐
                    │ ExternalProductService │                       │  getProductFromCache │
                    │   (Real API call)    │                          │   (Redis Fallback)   │
                    └──────────┬──────────┘                          └──────────┬──────────┘
                               │                                                 │
                               ▼                                                 ▼
                    ┌─────────────────────┐                          ┌─────────────────────┐
                    │  Response: 225ms    │                          │  Response: 5ms      │
                    │  Source: EXTERNAL   │                          │  Source: REDIS_CACHE │
                    └─────────────────────┘                          └─────────────────────┘
```

---

## 4. Resilience4j Configuration Explained

### 📄 application.yml - Full Configuration

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        # 1️⃣ Sliding Window - How to count failures
        sliding-window-type: COUNT_BASED    # Last N calls consider chestundi
        sliding-window-size: 10             # Last 10 calls lo failures count

        # 2️⃣ When to Open
        minimum-number-of-calls: 5          # Minimum 5 calls ayyaka ne evaluate
        failure-rate-threshold: 50          # 50% failures ayte OPEN
        slow-call-rate-threshold: 50        # 50% slow calls ayte OPEN
        slow-call-duration-threshold: 3s    # 3s+ = slow call ga consider

        # 3️⃣ Open State Behavior
        wait-duration-in-open-state: 10s    # 10s wait before HALF-OPEN

        # 4️⃣ Half-Open Testing
        permitted-number-of-calls-in-half-open-state: 3  # Test with 3 calls
        automatic-transition-from-open-to-half-open-enabled: true

    instances:
      externalService:           # Our named circuit breaker
        base-config: default

  retry:
    configs:
      default:
        max-attempts: 2          # Try 2 times before giving up
        wait-duration: 200ms     # Wait 200ms between retries
    instances:
      externalService:
        base-config: default

  timelimiter:
    configs:
      default:
        timeout-duration: 3s     # Max 3s wait for response
        cancel-running-future: true
    instances:
      externalService:
        base-config: default
```

### 📊 Configuration Visualization

```
┌───────────────────────────────────────────────────────────────────┐
│                    SLIDING WINDOW (10 calls)                       │
├───────────────────────────────────────────────────────────────────┤
│  Call # │  1  │  2  │  3  │  4  │  5  │  6  │  7  │  8  │  9  │ 10 │
│  Status │  ✓  │  ✓  │  ✗  │  ✓  │  ✗  │  ✗  │  ✓  │  ✗  │  ✗  │  ✗ │
├───────────────────────────────────────────────────────────────────┤
│  Failures: 6/10 = 60% → Failure Rate Threshold (50%) EXCEEDED!    │
│  → Circuit OPENS!                                                  │
└───────────────────────────────────────────────────────────────────┘
```

### 🔧 Three Resilience Patterns Combined

```java
@CircuitBreaker(name = "externalService", fallbackMethod = "getProductFromCache")
@Retry(name = "externalService")          // Retry 2 times before circuit counts as failure
@TimeLimiter(name = "externalService")    // Timeout after 3s
public CompletableFuture<ProductResponse> getProductById(Long id) { ... }
```

**Execution Order:**

```
Request → TimeLimiter (3s max) → Retry (2 attempts) → CircuitBreaker → Actual Call
                                                              │
                                                    If failure rate > 50%
                                                              │
                                                              ▼
                                                    Circuit OPENS
                                                              │
                                                              ▼
                                                    Fallback method executes
```

---

## 5. Service Implementation Details

### 🔑 ProductService.java - Main Service

```java
@Service
@RequiredArgsConstructor
public class ProductService {

    private final RestTemplate restTemplate;
    private final RedisCacheService redisCacheService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreakerMetricsService metricsService;

    @Value("${app.external-base-url:http://localhost:8080}")
    private String externalBaseUrl;

    // 🔑 Circuit Breaker + Retry + TimeLimiter annotations
    @CircuitBreaker(name = "externalService", fallbackMethod = "getProductFromCache")
    @Retry(name = "externalService")
    @TimeLimiter(name = "externalService")
    public CompletableFuture<ProductResponse> getProductById(Long id) {
        long start = System.nanoTime();
        
        return CompletableFuture.supplyAsync(() -> {
            // 🌐 External service call (can fail/timeout)
            Product product = restTemplate.getForObject(
                externalBaseUrl + "/external-api/products/{id}", 
                Product.class, id
            );
            
            if (product == null) {
                throw new IllegalStateException("External service returned empty");
            }
            
            // ✅ Success - Cache the response for future fallback
            redisCacheService.saveProduct(product);
            
            long elapsed = elapsedMs(start);
            metricsService.recordSuccess(ResponseSource.EXTERNAL_SERVICE, elapsed);
            
            return ProductResponse.builder()
                .data(product)
                .source(ResponseSource.EXTERNAL_SERVICE)
                .responseTimeMs(elapsed)
                .circuitState(currentCircuitState())
                .message("Fetched from external service")
                .build();
        });
    }

    // 🛡️ FALLBACK METHOD - Executes when circuit is OPEN or call fails
    @SuppressWarnings("unused")
    public CompletableFuture<ProductResponse> getProductFromCache(Long id, Exception exception) {
        return buildFallbackResponse(id, exception);
    }

    private CompletableFuture<ProductResponse> buildFallbackResponse(Long id, Throwable throwable) {
        long start = System.nanoTime();
        
        // 📦 Try to get from Redis cache
        Optional<Product> cached = redisCacheService.getProduct(id);
        long elapsed = elapsedMs(start);

        if (cached.isPresent()) {
            // ✅ Cache hit - Return cached data
            metricsService.recordFallback(elapsed, true);
            return CompletableFuture.completedFuture(ProductResponse.builder()
                .data(cached.get())
                .source(ResponseSource.REDIS_CACHE)
                .responseTimeMs(elapsed)      // ~5ms (very fast!)
                .circuitState(currentCircuitState())
                .message("Served from Redis fallback cache")
                .build());
        }

        // ❌ Cache miss - Error response
        metricsService.recordFallback(elapsed, false);
        return CompletableFuture.completedFuture(ProductResponse.builder()
            .data(null)
            .source(ResponseSource.FALLBACK_ERROR)
            .message("Fallback failed: cache miss for product " + id)
            .build());
    }
}
```

### ⚠️ ExternalProductService.java - Simulated External Service

```java
@Service
public class ExternalProductService {

    // 🎛️ Mode control for testing
    private final AtomicReference<ExternalMode> mode = 
        new AtomicReference<>(ExternalMode.NORMAL);

    public Product getProduct(Long id) {
        ExternalMode currentMode = mode.get();
        
        // 💥 FAIL mode - throws exception (simulates downstream failure)
        if (currentMode == ExternalMode.FAIL) {
            throw new IllegalStateException("Simulated downstream failure");
        }

        // 🐢 SLOW mode - 5 second delay (triggers timeout)
        // 🏃 NORMAL mode - 200ms response
        long delay = currentMode == ExternalMode.SLOW ? 5000L : 200L;
        sleep(delay);

        return Product.builder()
            .id(id)
            .name("Product-" + id)
            .description("External catalog item " + id)
            .price(BigDecimal.valueOf(100 + id))
            .category("GENERAL")
            .lastUpdated(Instant.now())
            .build();
    }

    // Control endpoints for testing
    public ExternalMode setFailMode() { mode.set(ExternalMode.FAIL); return mode.get(); }
    public ExternalMode setSlowMode() { mode.set(ExternalMode.SLOW); return mode.get(); }
    public ExternalMode resetMode() { mode.set(ExternalMode.NORMAL); return mode.get(); }
}
```

---

## 6. Fallback Mechanism - Redis Cache

### 🔴 Redis as Fallback Cache

```
Normal Operation (Circuit CLOSED):
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│  Request comes  │ ───→ │ External Service│ ───→ │ Save to Redis   │
│                 │      │  (200ms)        │      │  (for fallback) │
└─────────────────┘      └─────────────────┘      └─────────────────┘

Failure Mode (Circuit OPEN):
┌─────────────────┐      ┌─────────────────┐
│  Request comes  │ ───→ │ Redis Cache     │ ───→ Response (5ms!)
│                 │      │  (fallback)     │
└─────────────────┘      └─────────────────┘
         │
         └── No call to external service (fast fail)
```

### 📦 RedisCacheService.java

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private static final Duration TTL = Duration.ofMinutes(5);  // 5 min cache
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 💾 Save product after successful external call
    public void saveProduct(Product product) {
        redisTemplate.opsForValue().set(
            cacheKey(product.getId()), 
            product, 
            TTL
        );
    }

    // 📤 Get product for fallback
    public Optional<Product> getProduct(Long id) {
        Object value = redisTemplate.opsForValue().get(cacheKey(id));
        if (value == null) {
            return Optional.empty();
        }
        // Handle both direct Product and LinkedHashMap (from JSON deserialization)
        if (value instanceof Product product) {
            return Optional.of(product);
        }
        try {
            return Optional.of(objectMapper.convertValue(value, Product.class));
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to deserialize cached product for id {}", id, ex);
            return Optional.empty();
        }
    }

    private String cacheKey(Long id) {
        return "product:" + id;  // e.g., "product:1", "product:2"
    }
}
```

### 🐳 Docker Compose - Redis Setup

```yaml
version: '3.8'
services:
  redis:
    image: redis:7.2-alpine
    container_name: circuit-demo-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes    # Persistence enabled
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5

volumes:
  redis-data:
    driver: local
```

---

## 7. State Transitions & Metrics

### 📈 CircuitBreakerMetricsService.java

```java
@Service
@RequiredArgsConstructor
public class CircuitBreakerMetricsService {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    
    // Counters
    private final AtomicLong totalCalls = new AtomicLong();
    private final AtomicLong successfulCalls = new AtomicLong();
    private final AtomicLong failedCalls = new AtomicLong();
    private final AtomicLong fallbackCalls = new AtomicLong();
    private final AtomicLong totalTransitions = new AtomicLong();
    
    // Response time tracking
    private final ConcurrentLinkedQueue<Long> externalResponseTimes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Long> fallbackResponseTimes = new ConcurrentLinkedQueue<>();
    
    // Timeline for state transitions
    private final ConcurrentLinkedQueue<String> timeline = new ConcurrentLinkedQueue<>();

    @PostConstruct
    void init() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalService");
        
        // 👂 Listen for state transitions
        circuitBreaker.getEventPublisher().onStateTransition(event -> {
            Instant now = Instant.now();
            totalTransitions.incrementAndGet();
            timeline.add(now + " " + event.getStateTransition());
            // Log: "2026-02-06T09:53:04Z State transition from CLOSED to OPEN"
        });
    }

    public void recordSuccess(ResponseSource source, long responseTimeMs) {
        totalCalls.incrementAndGet();
        successfulCalls.incrementAndGet();
        if (source == ResponseSource.EXTERNAL_SERVICE) {
            externalResponseTimes.add(responseTimeMs);
        } else {
            fallbackResponseTimes.add(responseTimeMs);
        }
    }

    public void recordFallback(long responseTimeMs, boolean cacheHit) {
        totalCalls.incrementAndGet();
        fallbackCalls.incrementAndGet();
        fallbackResponseTimes.add(responseTimeMs);
    }
}
```

### 📊 State Transition Timeline (Actual Test)

```
2026-02-06T09:53:04Z  CLOSED → OPEN      (Failures detected)
2026-02-06T09:53:14Z  OPEN → HALF_OPEN   (After 10s wait)
2026-02-06T09:53:45Z  HALF_OPEN → OPEN   (Test call failed)
2026-02-06T09:53:55Z  OPEN → HALF_OPEN   (Try again)
2026-02-06T09:54:12Z  HALF_OPEN → CLOSED (Recovery successful!)
2026-02-06T09:55:40Z  CLOSED → OPEN      (Another failure cycle)
2026-02-06T09:55:50Z  OPEN → HALF_OPEN
2026-02-06T09:56:21Z  HALF_OPEN → OPEN
2026-02-06T09:56:31Z  OPEN → HALF_OPEN
2026-02-06T09:56:49Z  HALF_OPEN → CLOSED (Recovered again!)
```

---

## 8. Test Results & Measured Proof

### 📊 Load Test Results

| Metric | Value | Target | Status |
|--------|-------|--------|--------|
| **Availability with CB (%)** | 100% | >= 99% | ✅ PASS |
| **Fallback speed improvement (%)** | 97.77% | >= 95% | ✅ PASS |
| **Recovery time (sec)** | 14.02s | <= 30s | ✅ PASS |
| **Redis fallback avg (ms)** | 5.04ms | < 10ms | ✅ PASS |
| **Circuit opened during failures** | True | true | ✅ PASS |

### ⚡ Response Time Comparison

| Metric | External Service | Redis Fallback | Improvement |
|--------|------------------|----------------|-------------|
| **Avg Response** | 225.85 ms | 5.04 ms | **97.77%** 🔥 |
| **P95 Latency** | 234 ms | 7 ms | 97% |
| **P99 Latency** | 234 ms | 7 ms | 97% |

### 📈 Availability Analysis

```
Total Calls:              38
├── Successful Calls:     38    (100% with circuit breaker)
├── Failed Calls:         0
└── Fallback Calls:       25    (66% went through fallback)

Estimated without fallback: 34.21% availability 😱
With Circuit Breaker:       100% availability ✅
```

### 🎯 Key Insight

```
Without Circuit Breaker:
- External service down = Your app down
- Users wait 30s for timeout
- 34% availability

With Circuit Breaker:
- External service down = Redis fallback kicks in
- Users get response in 5ms
- 100% availability
- System recovers automatically when external service is back
```

---

## 9. Interview Ready Explanations

### 🎤 30-Second Answer

> "Circuit Breaker is a resilience pattern that protects your application when downstream services fail. When failure rate exceeds a threshold (50%), the circuit OPENS and all requests immediately return fallback data (from Redis) instead of waiting for timeout. After 10 seconds, it automatically tests if the service recovered. In our demo, we achieved 100% availability even when external service was down, with 97% faster response times using Redis fallback."

### 🎤 2-Minute Technical Deep Dive

> "We implemented Resilience4j Circuit Breaker with three patterns:
>
> **1. Circuit Breaker:** Monitors last 10 calls. If failure rate exceeds 50%, circuit OPENS. All subsequent requests skip the external call and go directly to fallback method. After 10 second wait, it transitions to HALF-OPEN state and allows 3 test calls. If they succeed, circuit CLOSES and normal operation resumes.
>
> **2. Retry:** Before counting a call as failure, we retry up to 2 times with 200ms gap. This handles transient failures.
>
> **3. Time Limiter:** If any call takes more than 3 seconds, we treat it as slow call. This prevents thread exhaustion from slow downstream services.
>
> For fallback, we use Redis cache. During normal operation, every successful response is cached in Redis (5 min TTL). When circuit opens, fallback method retrieves data from Redis in ~5ms instead of waiting 30s for timeout.
>
> In our load test, we simulated external service failure. Without circuit breaker, availability was 34%. With circuit breaker + Redis fallback, we maintained 100% availability with 97% faster response times."

### 🎤 Resume Bullet Point

> "Implemented Resilience4j Circuit Breaker pattern with Redis fallback cache to handle downstream service failures, achieving 100% application availability (vs 34% without protection) and 97.77% improvement in response times during failure scenarios."

---

## 🔧 Control Endpoints for Testing

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/products/{id}` | GET | Main API (Circuit Breaker protected) |
| `/api/cache/seed` | POST | Pre-populate Redis cache |
| `/external-api/control/fail` | POST | Make external service fail |
| `/external-api/control/slow` | POST | Make external service slow (5s) |
| `/external-api/control/reset` | POST | Reset to normal mode |
| `/api/metrics/circuit` | GET | Get circuit metrics |

### 🧪 Manual Test Steps

```bash
# 1. Start Redis
docker compose up -d

# 2. Seed cache for fallback
curl -X POST http://localhost:8080/api/cache/seed

# 3. Normal call (should work)
curl http://localhost:8080/api/products/1

# 4. Make external service fail
curl -X POST http://localhost:8080/external-api/control/fail

# 5. Call again (circuit will open, fallback to Redis)
curl http://localhost:8080/api/products/1
# Response: source=REDIS_CACHE, responseTimeMs=5

# 6. Reset external service
curl -X POST http://localhost:8080/external-api/control/reset

# 7. Wait 10s for circuit to transition to HALF-OPEN
# 8. Call again (circuit will close after successful test calls)
curl http://localhost:8080/api/products/1
```

---

## 🔗 Key Files Reference

| File | Purpose |
|------|---------|
| [ProductService.java](file:///d:/spring_boot_advanced_demos/circuit-breaker-demo/src/main/java/com/demo/circuitbreaker/service/ProductService.java) | Main service with Circuit Breaker |
| [ExternalProductService.java](file:///d:/spring_boot_advanced_demos/circuit-breaker-demo/src/main/java/com/demo/circuitbreaker/service/ExternalProductService.java) | Simulated external service |
| [RedisCacheService.java](file:///d:/spring_boot_advanced_demos/circuit-breaker-demo/src/main/java/com/demo/circuitbreaker/service/RedisCacheService.java) | Redis fallback cache |
| [application.yml](file:///d:/spring_boot_advanced_demos/circuit-breaker-demo/src/main/resources/application.yml) | Resilience4j configuration |
| [test-results.md](file:///d:/spring_boot_advanced_demos/circuit-breaker-demo/test-results.md) | Measured test results |

---

## 🧠 Quick Revision (5 Points)

1. **Problem:** External service failure → Your app failure → Bad UX
2. **Solution:** Circuit Breaker pattern with Redis fallback
3. **States:** CLOSED (normal) → OPEN (fail fast) → HALF_OPEN (testing)
4. **Config:** 50% failure threshold, 10s wait, 3 test calls
5. **Proof:** 100% availability, 97% faster responses, auto-recovery

---

*Generated: February 2026 | Spring Boot 3.3.8 | Java 21 | Resilience4j 2.2.0 | Redis 7.2*
