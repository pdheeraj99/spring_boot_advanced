package com.demo.circuitbreaker.service;

import com.demo.circuitbreaker.config.HitCounterProperties;
import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.hit-counter.mode", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryHitCounterService implements HitCounterService {

    private final HitCounterProperties properties;
    private final Clock clock;
    private final ConcurrentHashMap<String, CounterWindow> windowsByKey = new ConcurrentHashMap<>();

    @Override
    public void recordHit(String counterKey) {
        long epochSecond = clock.instant().getEpochSecond();
        CounterWindow window = windowsByKey.computeIfAbsent(counterKey, key -> new CounterWindow(properties.getWindowSeconds()));
        window.increment(epochSecond);
    }

    @Override
    public long getHits(String counterKey) {
        CounterWindow window = windowsByKey.get(counterKey);
        if (window == null) {
            return 0L;
        }
        long epochSecond = clock.instant().getEpochSecond();
        return window.sum(epochSecond, properties.getWindowSeconds());
    }

    static final class CounterWindow {
        private final Bucket[] buckets;

        private CounterWindow(int windowSeconds) {
            this.buckets = new Bucket[windowSeconds];
            for (int i = 0; i < windowSeconds; i++) {
                buckets[i] = new Bucket();
            }
        }

        void increment(long epochSecond) {
            Bucket bucket = buckets[(int) (epochSecond % buckets.length)];
            bucket.increment(epochSecond);
        }

        long sum(long nowEpochSecond, int windowSeconds) {
            long total = 0;
            for (Bucket bucket : buckets) {
                total += bucket.readIfWithinWindow(nowEpochSecond, windowSeconds);
            }
            return total;
        }
    }

    static final class Bucket {
        private long timestamp = -1;
        private long count = 0;

        synchronized void increment(long epochSecond) {
            if (timestamp != epochSecond) {
                timestamp = epochSecond;
                count = 0;
            }
            count++;
        }

        synchronized long readIfWithinWindow(long nowEpochSecond, int windowSeconds) {
            return nowEpochSecond - timestamp < windowSeconds ? count : 0;
        }
    }
}
