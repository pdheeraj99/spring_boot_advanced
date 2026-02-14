package com.demo.circuitbreaker.controller;

import com.demo.circuitbreaker.model.ExternalMode;
import com.demo.circuitbreaker.model.Product;
import com.demo.circuitbreaker.service.ExternalProductService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external-api")
@RequiredArgsConstructor
public class ExternalProductController {

    private final ExternalProductService externalProductService;

    @GetMapping("/products/{id}")
    public Product getExternalProduct(@PathVariable Long id) {
        return externalProductService.getProduct(id);
    }

    @PostMapping("/control/fail")
    public Map<String, ExternalMode> fail() {
        return Map.of("mode", externalProductService.setFailMode());
    }

    @PostMapping("/control/slow")
    public Map<String, ExternalMode> slow() {
        return Map.of("mode", externalProductService.setSlowMode());
    }

    @PostMapping("/control/reset")
    public Map<String, ExternalMode> reset() {
        return Map.of("mode", externalProductService.resetMode());
    }
}
