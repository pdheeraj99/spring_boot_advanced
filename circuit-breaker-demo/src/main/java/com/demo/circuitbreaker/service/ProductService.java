package com.demo.circuitbreaker.service;

import com.demo.circuitbreaker.model.Product;
import com.demo.circuitbreaker.model.ProductResponse;
import com.demo.circuitbreaker.model.ResponseSource;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final RestTemplate restTemplate;
    private final RedisCacheService redisCacheService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreakerMetricsService metricsService;

    @Value("${app.external-base-url:http://localhost:8080}")
    private String externalBaseUrl;

    @CircuitBreaker(name = "externalService", fallbackMethod = "getProductFromCache")
    @Retry(name = "externalService")
    @TimeLimiter(name = "externalService")
    public CompletableFuture<ProductResponse> getProductById(Long id) {
        long start = System.nanoTime();
        return CompletableFuture.supplyAsync(() -> {
            Product product = restTemplate.getForObject(
                externalBaseUrl + "/external-api/products/{id}", Product.class, id
            );
            if (product == null) {
                throw new IllegalStateException("External service returned empty response");
            }
            redisCacheService.saveProduct(product);
            long elapsed = elapsedMs(start);
            metricsService.recordSuccess(ResponseSource.EXTERNAL_SERVICE, elapsed);
            return ProductResponse.builder()
                .data(product)
                .source(ResponseSource.EXTERNAL_SERVICE)
                .responseTimeMs(elapsed)
                .circuitState(currentCircuitState())
                .message("Fetched from external service")
                .build();
        });
    }

    @SuppressWarnings("unused")
    public CompletableFuture<ProductResponse> getProductFromCache(Long id, Exception exception) {
        return buildFallbackResponse(id, exception);
    }

    @SuppressWarnings("unused")
    public CompletableFuture<ProductResponse> getProductFromCache(Long id) {
        return buildFallbackResponse(id, null);
    }

    private CompletableFuture<ProductResponse> buildFallbackResponse(Long id, Throwable throwable) {
        long start = System.nanoTime();
        Optional<Product> cached = redisCacheService.getProduct(id);
        long elapsed = elapsedMs(start);

        if (cached.isPresent()) {
            metricsService.recordFallback(elapsed, true);
            return CompletableFuture.completedFuture(ProductResponse.builder()
                .data(cached.get())
                .source(ResponseSource.REDIS_CACHE)
                .responseTimeMs(elapsed)
                .circuitState(currentCircuitState())
                .message("Served from Redis fallback cache")
                .build());
        }

        metricsService.recordFallback(elapsed, false);
        return CompletableFuture.completedFuture(ProductResponse.builder()
            .data(null)
            .source(ResponseSource.FALLBACK_ERROR)
            .responseTimeMs(elapsed)
            .circuitState(currentCircuitState())
            .message("Fallback failed: cache miss for product " + id + ". Cause: " + rootCause(throwable))
            .build());
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private String currentCircuitState() {
        return circuitBreakerRegistry.circuitBreaker("externalService").getState().name();
    }

    private String rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current != null && current.getCause() != null) {
            current = current.getCause();
        }
        return current == null ? "unknown" : current.getMessage();
    }
}
