package com.example.nplus1demo.metrics;

public record RequestMetricsSnapshot(
        String requestId,
        String path,
        long sqlCount,
        long executionTimeMs,
        long connectionWaitMs,
        int status,
        long timestampMs) {
}
