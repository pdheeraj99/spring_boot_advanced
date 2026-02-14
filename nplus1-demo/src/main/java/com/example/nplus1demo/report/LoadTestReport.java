package com.example.nplus1demo.report;

import java.util.Map;

public record LoadTestReport(
        LoadTestScenarioResult nPlusOne,
        LoadTestScenarioResult optimized,
        double queryReductionPercent,
        double executionImprovementPercent,
        double connectionWaitReductionPercent,
        boolean queryReductionPass,
        boolean executionImprovementPass,
        boolean connectionWaitReductionPass,
        boolean simulatedLatencyEnabled,
        Map<String, Object> hibernateStats) {
}
