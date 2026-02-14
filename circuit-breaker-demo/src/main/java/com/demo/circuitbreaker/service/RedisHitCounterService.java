package com.demo.circuitbreaker.service;

import com.demo.circuitbreaker.config.HitCounterProperties;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.hit-counter.mode", havingValue = "redis")
public class RedisHitCounterService implements HitCounterService {

    private final StringRedisTemplate redisTemplate;
    private final HitCounterProperties properties;
    private final Clock clock;

    @Override
    public void recordHit(String counterKey) {
        long epochSecond = clock.instant().getEpochSecond();
        String key = redisKey(counterKey);
        String field = Long.toString(epochSecond);
        redisTemplate.opsForHash().increment(key, field, 1);
        redisTemplate.expire(key, Duration.ofSeconds(properties.getWindowSeconds() + 10L));
    }

    @Override
    public long getHits(String counterKey) {
        long nowEpochSecond = clock.instant().getEpochSecond();
        String key = redisKey(counterKey);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return 0L;
        }

        long total = 0;
        List<String> staleFields = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            long epochSecond = Long.parseLong(String.valueOf(entry.getKey()));
            long value = Long.parseLong(String.valueOf(entry.getValue()));
            if (nowEpochSecond - epochSecond < properties.getWindowSeconds()) {
                total += value;
            } else {
                staleFields.add(String.valueOf(entry.getKey()));
            }
        }

        if (!staleFields.isEmpty()) {
            redisTemplate.opsForHash().delete(key, staleFields.toArray());
        }

        return total;
    }

    private String redisKey(String counterKey) {
        return properties.getRedisKeyPrefix() + ":" + counterKey;
    }
}
