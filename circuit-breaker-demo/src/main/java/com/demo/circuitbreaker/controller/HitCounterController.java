package com.demo.circuitbreaker.controller;

import com.demo.circuitbreaker.config.HitCounterProperties;
import com.demo.circuitbreaker.model.HitCounterResponse;
import com.demo.circuitbreaker.service.HitCounterService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hit-counter")
@RequiredArgsConstructor
@Validated
public class HitCounterController {

    private final HitCounterService hitCounterService;
    private final HitCounterProperties properties;

    @Value("${app.hit-counter.mode:in-memory}")
    private String mode;

    @PostMapping("/{counterKey}/hit")
    public HitCounterResponse recordHit(@PathVariable String counterKey) {
        hitCounterService.recordHit(counterKey);
        return snapshot(counterKey);
    }

    @GetMapping("/{counterKey}")
    public HitCounterResponse getHits(@PathVariable String counterKey) {
        return snapshot(counterKey);
    }

    private HitCounterResponse snapshot(String counterKey) {
        long hits = hitCounterService.getHits(counterKey);
        return new HitCounterResponse(
            counterKey,
            hits,
            properties.getWindowSeconds(),
            mode,
            Instant.now()
        );
    }
}
