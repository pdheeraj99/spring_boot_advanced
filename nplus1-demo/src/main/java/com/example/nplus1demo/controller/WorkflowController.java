package com.example.nplus1demo.controller;

import com.example.nplus1demo.report.LoadTestReport;
import com.example.nplus1demo.service.ExecutionLogService;
import com.example.nplus1demo.service.LoadTestService;
import com.example.nplus1demo.service.ReportService;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class WorkflowController {

    private final LoadTestService loadTestService;
    private final ReportService reportService;
    private final ExecutionLogService executionLogService;

    @PostMapping("/run-workflow")
    public Map<String, Object> runWorkflow() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = new LinkedHashMap<>();

        executionLogService.logStep("STEP 4: Warm up application");
        restTemplate.getForObject("http://localhost:8080/actuator/health", String.class);
        sleep(5);

        executionLogService.logStep("STEP 5: Test N+1 endpoint");
        long v1Start = System.currentTimeMillis();
        restTemplate.getForObject("http://localhost:8080/api/v1/orders/n-plus-one", String.class);
        long v1Time = System.currentTimeMillis() - v1Start;
        sleep(1);

        executionLogService.logStep("STEP 6: Test optimized endpoint");
        long v2Start = System.currentTimeMillis();
        restTemplate.getForObject("http://localhost:8080/api/v2/orders/optimized", String.class);
        long v2Time = System.currentTimeMillis() - v2Start;
        sleep(1);

        executionLogService.logStep("STEP 7: Run load tests");
        LoadTestReport report = loadTestService.run();

        executionLogService.logStep("STEP 8: Generate report files");
        reportService.writeReports(report);

        response.put("singleCallV1Ms", v1Time);
        response.put("singleCallV2Ms", v2Time);
        response.put("report", report);
        return response;
    }

    private void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
