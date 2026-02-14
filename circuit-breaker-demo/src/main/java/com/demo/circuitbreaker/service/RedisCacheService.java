package com.demo.circuitbreaker.service;

import com.demo.circuitbreaker.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private static final Duration TTL = Duration.ofMinutes(5);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveProduct(Product product) {
        redisTemplate.opsForValue().set(cacheKey(product.getId()), product, TTL);
    }

    public Optional<Product> getProduct(Long id) {
        Object value = redisTemplate.opsForValue().get(cacheKey(id));
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Product product) {
            return Optional.of(product);
        }
        try {
            return Optional.of(objectMapper.convertValue(value, Product.class));
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to deserialize cached product for id {}", id, ex);
            return Optional.empty();
        }
    }

    private String cacheKey(Long id) {
        return "product:" + id;
    }
}
