package com.demo.circuitbreaker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.circuitbreaker.config.HitCounterProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class InMemoryHitCounterServiceTest {

    @Test
    void shouldCountHitsWithinWindow() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-11T10:00:00Z"));
        HitCounterProperties properties = new HitCounterProperties();
        properties.setWindowSeconds(5);
        InMemoryHitCounterService service = new InMemoryHitCounterService(properties, clock);

        service.recordHit("orders");
        service.recordHit("orders");
        assertEquals(2L, service.getHits("orders"));
    }

    @Test
    void shouldExpireHitsOutsideWindow() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-11T10:00:00Z"));
        HitCounterProperties properties = new HitCounterProperties();
        properties.setWindowSeconds(3);
        InMemoryHitCounterService service = new InMemoryHitCounterService(properties, clock);

        service.recordHit("checkout");
        clock.plusSeconds(2);
        service.recordHit("checkout");
        assertEquals(2L, service.getHits("checkout"));

        clock.plusSeconds(2);
        assertEquals(1L, service.getHits("checkout"));

        clock.plusSeconds(2);
        assertEquals(0L, service.getHits("checkout"));
    }

    private static final class MutableClock extends Clock {
        private Instant current;
        private final ZoneId zone;

        private MutableClock(Instant initial) {
            this(initial, ZoneOffset.UTC);
        }

        private MutableClock(Instant initial, ZoneId zone) {
            this.current = initial;
            this.zone = zone;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(current, zone);
        }

        @Override
        public Instant instant() {
            return current;
        }

        private void plusSeconds(long seconds) {
            current = current.plusSeconds(seconds);
        }
    }
}
