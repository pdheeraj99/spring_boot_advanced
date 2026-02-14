package com.demo.circuitbreaker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Instant lastUpdated;
}
