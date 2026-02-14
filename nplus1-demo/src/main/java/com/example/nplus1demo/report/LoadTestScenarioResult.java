package com.example.nplus1demo.report;

public record LoadTestScenarioResult(
        String endpoint,
        int totalRequests,
        double avgLatencyMs,
        double p95LatencyMs,
        double p99LatencyMs,
        double avgQueriesPerRequest,
        double avgConnectionWaitMs,
        long totalConnectionWaitMs,
        int errors) {
}
