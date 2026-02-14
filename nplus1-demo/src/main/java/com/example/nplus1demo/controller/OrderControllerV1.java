package com.example.nplus1demo.controller;

import com.example.nplus1demo.dto.OrderResponse;
import com.example.nplus1demo.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderService orderService;

    @GetMapping("/n-plus-one")
    public List<OrderResponse> getNPlusOneOrders() {
        return orderService.getAllOrdersWithNPlusOne(); // devtools-check-comment 20260206140459
    }
}
