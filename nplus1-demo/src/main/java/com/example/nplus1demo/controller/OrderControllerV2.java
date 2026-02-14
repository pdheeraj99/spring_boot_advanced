package com.example.nplus1demo.controller;

import com.example.nplus1demo.dto.OrderResponse;
import com.example.nplus1demo.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/orders")
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderService orderService;

    @GetMapping("/optimized")
    public List<OrderResponse> getOptimizedOrders() {
        return orderService.getAllOrdersOptimized();
    }
}
