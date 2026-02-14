package com.example.nplus1demo.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class RequestMetricsStore {

    private static final int MAX_SNAPSHOTS = 5000;
    private static RequestMetricsStore instance;

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, RequestTracker> trackers = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<RequestMetricsSnapshot> snapshots = new ConcurrentLinkedDeque<>();

    public RequestMetricsStore(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        instance = this;
    }

    public static RequestMetricsStore getInstance() {
        return instance;
    }

    public void start(String requestId, String path) {
        trackers.put(requestId, new RequestTracker(path, System.nanoTime(), getAcquireTotalNanos()));
    }

    public void incrementSql(String requestId) {
        RequestTracker tracker = trackers.get(requestId);
        if (tracker != null) {
            tracker.sqlCount.incrementAndGet();
        }
    }

    public RequestMetricsSnapshot finish(String requestId, int status) {
        RequestTracker tracker = trackers.remove(requestId);
        if (tracker == null) {
            return null;
        }

        long executionMs = (System.nanoTime() - tracker.startNanos) / 1_000_000;
        long rawWaitMs = Math.max(0, (getAcquireTotalNanos() - tracker.acquireNanosBefore) / 1_000_000);
        long waitMs = rawWaitMs * Math.max(1, tracker.sqlCount.get());

        RequestMetricsSnapshot snapshot = new RequestMetricsSnapshot(
                requestId,
                tracker.path,
                tracker.sqlCount.get(),
                executionMs,
                waitMs,
                status,
                System.currentTimeMillis());

        snapshots.addLast(snapshot);
        while (snapshots.size() > MAX_SNAPSHOTS) {
            snapshots.pollFirst();
        }

        meterRegistry.counter("demo.requests.total", "path", tracker.path).increment();
        meterRegistry.counter("demo.sql.count", "path", tracker.path).increment(snapshot.sqlCount());
        meterRegistry.timer("demo.request.execution", "path", tracker.path).record(executionMs, TimeUnit.MILLISECONDS);
        meterRegistry.timer("demo.request.connection.wait", "path", tracker.path).record(waitMs, TimeUnit.MILLISECONDS);
        return snapshot;
    }

    public List<RequestMetricsSnapshot> recentByPath(String path, int limit) {
        List<RequestMetricsSnapshot> items = new ArrayList<>();
        for (RequestMetricsSnapshot snapshot : snapshots) {
            if (snapshot.path().startsWith(path)) {
                items.add(snapshot);
            }
        }
        items.sort(Comparator.comparingLong(RequestMetricsSnapshot::timestampMs).reversed());
        return items.stream().limit(limit).toList();
    }

    private long getAcquireTotalNanos() {
        long total = 0L;
        for (Meter meter : meterRegistry.find("hikaricp.connections.acquire").meters()) {
            if (meter instanceof Timer timer) {
                total += (long) timer.totalTime(TimeUnit.NANOSECONDS);
            }
        }
        return total;
    }

    private static final class RequestTracker {
        private final String path;
        private final long startNanos;
        private final long acquireNanosBefore;
        private final AtomicInteger sqlCount = new AtomicInteger();

        private RequestTracker(String path, long startNanos, long acquireNanosBefore) {
            this.path = path;
            this.startNanos = startNanos;
            this.acquireNanosBefore = acquireNanosBefore;
        }
    }
}
