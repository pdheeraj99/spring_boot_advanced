package com.demo.circuitbreaker;

import com.demo.circuitbreaker.config.HitCounterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(HitCounterProperties.class)
public class CircuitBreakerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CircuitBreakerDemoApplication.class, args);
    }
}
