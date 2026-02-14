package com.example.nplus1demo.service;

import com.example.nplus1demo.metrics.RequestMetricsStore;
import com.example.nplus1demo.report.LoadTestReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ObjectMapper objectMapper;
    private final RequestMetricsStore metricsStore;

    public void writeReports(LoadTestReport report) {
        try {
            Map<String, Object> output = new LinkedHashMap<>();
            output.put("generatedAt", LocalDateTime.now().toString());
            output.put("report", report);
            output.put("recentV1Metrics", metricsStore.recentByPath("/api/v1/orders/n-plus-one", 50));
            output.put("recentV2Metrics", metricsStore.recentByPath("/api/v2/orders/optimized", 50));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(Path.of("test-results.json").toFile(), output);

            String md = "# N+1 Optimization Test Results\n"
                    + "Generated: " + LocalDateTime.now() + "\n\n"
                    + "| Metric | V1 N+1 | V2 Optimized | Improvement | Target | Status |\n"
                    + "|---|---:|---:|---:|---:|---|\n"
                    + "| Avg Queries/Request | " + fmt(report.nPlusOne().avgQueriesPerRequest()) + " | " + fmt(report.optimized().avgQueriesPerRequest()) + " | " + fmt(report.queryReductionPercent()) + "% | >=80% | " + status(report.queryReductionPass()) + " |\n"
                    + "| Avg Response Time (ms) | " + fmt(report.nPlusOne().avgLatencyMs()) + " | " + fmt(report.optimized().avgLatencyMs()) + " | " + fmt(report.executionImprovementPercent()) + "% | >=73% | " + status(report.executionImprovementPass()) + " |\n"
                    + "| Avg Connection Wait (ms) | " + fmt(report.nPlusOne().avgConnectionWaitMs()) + " | " + fmt(report.optimized().avgConnectionWaitMs()) + " | " + fmt(report.connectionWaitReductionPercent()) + "% | >=98% | " + status(report.connectionWaitReductionPass()) + " |\n\n"
                    + "## Latency Percentiles\n"
                    + "- V1 P95: " + fmt(report.nPlusOne().p95LatencyMs()) + " ms\n"
                    + "- V1 P99: " + fmt(report.nPlusOne().p99LatencyMs()) + " ms\n"
                    + "- V2 P95: " + fmt(report.optimized().p95LatencyMs()) + " ms\n"
                    + "- V2 P99: " + fmt(report.optimized().p99LatencyMs()) + " ms\n\n"
                    + "## Hibernate Statistics\n"
                    + "```json\n"
                    + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report.hibernateStats())
                    + "\n```\n";

            Files.writeString(Path.of("test-results.md"), md);
        } catch (Exception e) {
            throw new RuntimeException("Unable to write reports", e);
        }
    }

    private String status(boolean pass) {
        return pass ? "PASS" : "FAIL";
    }

    private String fmt(double value) {
        return String.format("%.2f", value);
    }
}
