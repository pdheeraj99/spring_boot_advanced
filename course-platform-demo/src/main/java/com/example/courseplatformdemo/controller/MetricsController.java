package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.QueryComparisonResponse;
import com.example.courseplatformdemo.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/latest")
    public QueryComparisonResponse latest() {
        return metricsService.getLatest();
    }
}