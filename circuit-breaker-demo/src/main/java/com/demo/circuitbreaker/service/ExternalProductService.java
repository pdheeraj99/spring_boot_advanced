package com.demo.circuitbreaker.service;

import com.demo.circuitbreaker.model.ExternalMode;
import com.demo.circuitbreaker.model.Product;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;

@Service
public class ExternalProductService {

    private final AtomicReference<ExternalMode> mode = new AtomicReference<>(ExternalMode.NORMAL);

    public Product getProduct(Long id) {
        ExternalMode currentMode = mode.get();
        if (currentMode == ExternalMode.FAIL) {
            throw new IllegalStateException("Simulated downstream failure");
        }

        long delay = currentMode == ExternalMode.SLOW ? 5000L : 200L;
        sleep(delay);

        return Product.builder()
            .id(id)
            .name("Product-" + id)
            .description("External catalog item " + id)
            .price(BigDecimal.valueOf(100 + id))
            .category("GENERAL")
            .lastUpdated(Instant.now())
            .build();
    }

    public ExternalMode setFailMode() {
        mode.set(ExternalMode.FAIL);
        return mode.get();
    }

    public ExternalMode setSlowMode() {
        mode.set(ExternalMode.SLOW);
        return mode.get();
    }

    public ExternalMode resetMode() {
        mode.set(ExternalMode.NORMAL);
        return mode.get();
    }

    public ExternalMode getMode() {
        return mode.get();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("External call interrupted", e);
        }
    }
}
