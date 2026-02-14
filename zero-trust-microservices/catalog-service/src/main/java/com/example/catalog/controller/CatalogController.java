package com.example.catalog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/catalog")
public class CatalogController {

    @GetMapping("/items")
    public List<Map<String, Object>> items(Authentication authentication) {
        return List.of(
                Map.of("sku", "SKU-1", "name", "Mesh Handbook", "caller", authentication.getName()),
                Map.of("sku", "SKU-2", "name", "OAuth2 Notes", "caller", authentication.getName()));
    }

    @GetMapping("/admin/report")
    public Map<String, Object> adminReport(Authentication authentication) {
        return Map.of(
                "status", "ok",
                "report", "Catalog admin report",
                "requestedBy", authentication.getName());
    }
}