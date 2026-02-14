package com.demo.circuitbreaker.model;

import java.time.Instant;

public record HitCounterResponse(
    String counterKey,
    long hits,
    int windowSeconds,
    String mode,
    Instant measuredAt
) {
}
