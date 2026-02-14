package com.demo.circuitbreaker.controller;

import com.demo.circuitbreaker.service.CircuitBreakerMetricsService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final CircuitBreakerMetricsService metricsService;

    @GetMapping("/circuit-breaker")
    public Map<String, Object> getCircuitBreakerMetrics() {
        return metricsService.snapshot();
    }
}
