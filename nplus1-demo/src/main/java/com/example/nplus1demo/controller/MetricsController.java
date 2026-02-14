package com.example.nplus1demo.controller;

import com.example.nplus1demo.metrics.RequestMetricsSnapshot;
import com.example.nplus1demo.metrics.RequestMetricsStore;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final RequestMetricsStore requestMetricsStore;

    @GetMapping("/recent")
    public List<RequestMetricsSnapshot> recent(@RequestParam String path, @RequestParam(defaultValue = "20") int limit) {
        return requestMetricsStore.recentByPath(path, limit);
    }
}
