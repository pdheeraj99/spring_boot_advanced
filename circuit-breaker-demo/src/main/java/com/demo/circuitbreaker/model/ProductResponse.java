package com.demo.circuitbreaker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Product data;
    private ResponseSource source;
    private long responseTimeMs;
    private String circuitState;
    private String message;
}
