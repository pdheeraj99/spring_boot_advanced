package com.example.nplus1demo.controller;

import com.example.nplus1demo.report.LoadTestReport;
import com.example.nplus1demo.service.LoadTestService;
import com.example.nplus1demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class UnifiedTestController {

    private final LoadTestService loadTestService;
    private final ReportService reportService;

    @PostMapping("/run-load-test")
    public LoadTestReport runLoadTest() {
        LoadTestReport report = loadTestService.run();
        reportService.writeReports(report);
        return report;
    }
}
