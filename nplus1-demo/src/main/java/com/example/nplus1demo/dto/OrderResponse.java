package com.example.nplus1demo.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderResponse(Long id,
                            String orderNumber,
                            LocalDate orderDate,
                            String customerName,
                            List<OrderItemResponse> orderItems,
                            List<OrderAuditResponse> orderAudits) {
}
