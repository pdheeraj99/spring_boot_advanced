package com.demo.circuitbreaker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.hit-counter")
@Getter
@Setter
public class HitCounterProperties {

    private int windowSeconds = 300;
    private String redisKeyPrefix = "hit-counter";
}
