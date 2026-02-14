package com.example.inventory.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory")
public class InventoryController {

    @GetMapping("/stock/{sku}")
    public Map<String, Object> stock(@PathVariable String sku, Authentication authentication) {
        return Map.of(
                "sku", sku,
                "available", true,
                "quantity", 42,
                "checkedBy", authentication.getName());
    }
}