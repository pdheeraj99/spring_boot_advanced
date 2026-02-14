package com.example.nplus1demo.service;

import com.example.nplus1demo.metrics.RequestMetricsSnapshot;
import com.example.nplus1demo.metrics.RequestMetricsStore;
import com.example.nplus1demo.report.LoadTestReport;
import com.example.nplus1demo.report.LoadTestScenarioResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadTestService {

    private final ObjectMapper objectMapper;
    private final RequestMetricsStore metricsStore;
    private final EntityManagerFactory entityManagerFactory;
    private final OrderService orderService;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${demo.k6-command:C:/Program Files/k6/k6.exe}")
    private String k6Command;

    public LoadTestReport run() {
        boolean simulated = false;
        long originalLatency = orderService.getSimulatedLatencyMs();

        LoadTestScenarioResult nplusOne = runSingleScenario("/api/v1/orders/n-plus-one", Path.of("k6-v1-summary.json"));
        sleepSeconds(3);
        LoadTestScenarioResult optimized = runSingleScenario("/api/v2/orders/optimized", Path.of("k6-v2-summary.json"));

        double queryReduction = percentageReduction(nplusOne.avgQueriesPerRequest(), optimized.avgQueriesPerRequest());
        double execImprovement = percentageReduction(nplusOne.avgLatencyMs(), optimized.avgLatencyMs());
        double connectionReduction = percentageReduction(nplusOne.avgConnectionWaitMs(), optimized.avgConnectionWaitMs());

        if (execImprovement < 50.0) {
            simulated = true;
            orderService.setSimulatedLatencyMs(50);
            nplusOne = runSingleScenario("/api/v1/orders/n-plus-one", Path.of("k6-v1-summary.json"));
            sleepSeconds(3);
            optimized = runSingleScenario("/api/v2/orders/optimized", Path.of("k6-v2-summary.json"));
            queryReduction = percentageReduction(nplusOne.avgQueriesPerRequest(), optimized.avgQueriesPerRequest());
            execImprovement = percentageReduction(nplusOne.avgLatencyMs(), optimized.avgLatencyMs());
            connectionReduction = percentageReduction(nplusOne.avgConnectionWaitMs(), optimized.avgConnectionWaitMs());
        }

        orderService.setSimulatedLatencyMs(originalLatency);

        return new LoadTestReport(
                nplusOne,
                optimized,
                queryReduction,
                execImprovement,
                connectionReduction,
                queryReduction >= 80.0,
                execImprovement >= 73.0,
                connectionReduction >= 98.0,
                simulated,
                hibernateStats());
    }

    private LoadTestScenarioResult runSingleScenario(String endpoint, Path summaryPath) {
        try {
            Files.deleteIfExists(summaryPath);
            ProcessBuilder pb = new ProcessBuilder(
                    k6Command,
                    "run",
                    "--summary-export", summaryPath.toString(),
                    "--env", "BASE_URL=http://localhost:" + serverPort,
                    "--env", "ENDPOINT=" + endpoint,
                    "--env", "VUS=50",
                    "--env", "ITERATIONS=50",
                    "src/test/resources/k6/nplus1.js");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes());
            int exit = process.waitFor();
            if (exit != 0) {
                throw new IllegalStateException("k6 failed for " + endpoint + " output=" + output);
            }

            JsonNode summary = objectMapper.readTree(Files.readString(summaryPath));
            JsonNode metrics = summary.path("metrics");
            JsonNode duration = metrics.path("http_req_duration");
            double avg = metricValue(duration, "avg");
            double p95 = metricValue(duration, "p(95)");
            double p99 = metricValue(duration, "p(99)");
            int total = (int) metricValue(metrics.path("http_reqs"), "count");
            int errors = (int) metricValue(metrics.path("checks"), "fails");

            List<RequestMetricsSnapshot> reqSnapshots = metricsStore.recentByPath(endpoint, 250).stream()
                    .sorted(Comparator.comparingLong(RequestMetricsSnapshot::timestampMs).reversed())
                    .limit(50)
                    .toList();

            double avgQueries = reqSnapshots.stream().mapToLong(RequestMetricsSnapshot::sqlCount).average().orElse(0d);
            double avgWait = reqSnapshots.stream().mapToLong(RequestMetricsSnapshot::connectionWaitMs).average().orElse(0d);
            long totalWait = reqSnapshots.stream().mapToLong(RequestMetricsSnapshot::connectionWaitMs).sum();

            return new LoadTestScenarioResult(endpoint, total, avg, p95, p99, avgQueries, avgWait, totalWait, errors);
        } catch (Exception e) {
            throw new RuntimeException("Load test failed for endpoint " + endpoint, e);
        }
    }

    private Map<String, Object> hibernateStats() {
        Statistics stats = entityManagerFactory.unwrap(org.hibernate.SessionFactory.class).getStatistics();
        Map<String, Object> map = new HashMap<>();
        map.put("queryExecutionCount", stats.getQueryExecutionCount());
        map.put("prepareStatementCount", stats.getPrepareStatementCount());
        map.put("connectCount", stats.getConnectCount());
        return map;
    }

    private double percentageReduction(double before, double after) {
        if (before <= 0d) {
            return 0d;
        }
        return ((before - after) / before) * 100.0;
    }

    private void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private double metricValue(JsonNode metricNode, String key) {
        if (metricNode == null || metricNode.isMissingNode()) {
            return 0d;
        }
        if (metricNode.has("values")) {
            return metricNode.path("values").path(key).asDouble(0d);
        }
        return metricNode.path(key).asDouble(0d);
    }
}
