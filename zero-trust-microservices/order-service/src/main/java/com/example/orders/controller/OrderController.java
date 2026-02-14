package com.example.orders.controller;

import java.util.Map;

import com.example.orders.model.OrderRequest;
import com.example.orders.service.InventoryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/orders")
public class OrderController {

    private final InventoryClient inventoryClient;

    public OrderController(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @PostMapping({"", "/"})
    public Map<String, Object> createOrder(@RequestBody OrderRequest request,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                           Authentication authentication) {

        Map inventory = inventoryClient.stock(request.sku(), authorization);

        return Map.of(
                "status", "created",
                "sku", request.sku(),
                "quantity", request.quantity(),
                "inventory", inventory,
                "createdBy", authentication.getName());
    }
}
