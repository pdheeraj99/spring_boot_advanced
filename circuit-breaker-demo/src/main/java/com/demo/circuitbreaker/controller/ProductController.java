package com.demo.circuitbreaker.controller;

import com.demo.circuitbreaker.model.Product;
import com.demo.circuitbreaker.model.ProductResponse;
import com.demo.circuitbreaker.service.ProductService;
import com.demo.circuitbreaker.service.RedisCacheService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RedisCacheService redisCacheService;

    @GetMapping("/products/{id}")
    public ProductResponse getProduct(@PathVariable Long id) throws Exception {
        return productService.getProductById(id).get(8, TimeUnit.SECONDS);
    }

    @GetMapping("/products/batch")
    public List<ProductResponse> getBatch(@RequestParam String ids) {
        return Arrays.stream(ids.split(","))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .map(Long::valueOf)
            .map(id -> {
                try {
                    return productService.getProductById(id).get(8, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new IllegalStateException("Batch call failed for id " + id, e);
                }
            })
            .toList();
    }

    @PostMapping("/cache/seed")
    public Map<String, Object> seedCache() {
        for (long id = 1; id <= 5; id++) {
            Product product = Product.builder()
                .id(id)
                .name("Seed-" + id)
                .description("Seeded product " + id)
                .price(BigDecimal.valueOf(50 + id))
                .category("SEEDED")
                .lastUpdated(Instant.now())
                .build();
            redisCacheService.saveProduct(product);
        }
        return Map.of("status", "seeded", "count", 5);
    }
}
// DevTools restart probe 2026-02-06 15:14:16
