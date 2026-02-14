package com.demo.circuitbreaker.service;

import com.demo.circuitbreaker.model.ResponseSource;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CircuitBreakerMetricsService {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final AtomicLong totalCalls = new AtomicLong();
    private final AtomicLong successfulCalls = new AtomicLong();
    private final AtomicLong failedCalls = new AtomicLong();
    private final AtomicLong fallbackCalls = new AtomicLong();
    private final AtomicLong totalTransitions = new AtomicLong();
    private final ConcurrentLinkedQueue<Long> externalResponseTimes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Long> fallbackResponseTimes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> timeline = new ConcurrentLinkedQueue<>();

    private volatile CircuitBreaker.State currentState = CircuitBreaker.State.CLOSED;
    private volatile Instant stateChangedAt = Instant.now();
    private final AtomicLong closedDurationSec = new AtomicLong();
    private final AtomicLong openDurationSec = new AtomicLong();
    private final AtomicLong halfOpenDurationSec = new AtomicLong();

    @PostConstruct
    void init() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalService");
        currentState = circuitBreaker.getState();
        stateChangedAt = Instant.now();

        circuitBreaker.getEventPublisher().onStateTransition(event -> {
            Instant now = Instant.now();
            long elapsed = Duration.between(stateChangedAt, now).toSeconds();
            if (currentState == CircuitBreaker.State.CLOSED) {
                closedDurationSec.addAndGet(Math.max(elapsed, 0));
            } else if (currentState == CircuitBreaker.State.OPEN) {
                openDurationSec.addAndGet(Math.max(elapsed, 0));
            } else if (currentState == CircuitBreaker.State.HALF_OPEN) {
                halfOpenDurationSec.addAndGet(Math.max(elapsed, 0));
            }
            currentState = event.getStateTransition().getToState();
            stateChangedAt = now;
            totalTransitions.incrementAndGet();
            timeline.add(now + " " + event.getStateTransition());
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
        failedCalls.incrementAndGet();
        fallbackResponseTimes.add(responseTimeMs);
        if (!cacheHit) {
            timeline.add(Instant.now() + " FALLBACK_CACHE_MISS");
        }
    }

    public Map<String, Object> snapshot() {
        long nowElapsed = Duration.between(stateChangedAt, Instant.now()).toSeconds();
        long closed = closedDurationSec.get();
        long open = openDurationSec.get();
        long halfOpen = halfOpenDurationSec.get();
        if (currentState == CircuitBreaker.State.CLOSED) {
            closed += Math.max(nowElapsed, 0);
        } else if (currentState == CircuitBreaker.State.OPEN) {
            open += Math.max(nowElapsed, 0);
        } else if (currentState == CircuitBreaker.State.HALF_OPEN) {
            halfOpen += Math.max(nowElapsed, 0);
        }

        return Map.of(
            "totalCalls", totalCalls.get(),
            "successfulCalls", successfulCalls.get(),
            "failedCalls", failedCalls.get(),
            "fallbackCalls", fallbackCalls.get(),
            "currentState", currentState.name(),
            "totalTransitions", totalTransitions.get(),
            "responseTimes", Map.of(
                "externalService", stats(externalResponseTimes),
                "redisFallback", stats(fallbackResponseTimes)
            ),
            "durations", Map.of(
                "closedDurationSec", closed,
                "openDurationSec", open,
                "halfOpenDurationSec", halfOpen
            ),
            "timeline", new ArrayList<>(timeline)
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Number> stats(ConcurrentLinkedQueue<Long> values) {
        if (values.isEmpty()) {
            return Map.of("avgMs", 0.0, "p95Ms", 0L, "p99Ms", 0L);
        }
        List<Long> sorted = new ArrayList<>(values);
        sorted.sort(Long::compareTo);
        double avg = sorted.stream().mapToLong(Long::longValue).average().orElse(0);
        long p95 = percentile(sorted, 95);
        long p99 = percentile(sorted, 99);
        return Map.of("avgMs", avg, "p95Ms", p95, "p99Ms", p99);
    }

    private long percentile(List<Long> sorted, int p) {
        if (sorted.isEmpty()) {
            return 0;
        }
        int idx = (int) Math.ceil((p / 100.0) * sorted.size()) - 1;
        idx = Math.max(0, Math.min(idx, sorted.size() - 1));
        return sorted.get(idx);
    }
}
