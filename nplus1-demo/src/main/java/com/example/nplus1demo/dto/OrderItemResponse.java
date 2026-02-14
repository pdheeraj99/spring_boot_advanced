package com.example.nplus1demo.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, String productName, int quantity, BigDecimal price, ProductResponse product) {
}
