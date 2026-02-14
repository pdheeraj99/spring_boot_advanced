package com.example.orders.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(@Value("${inventory.base-url:http://inventory-service:8080}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public Map stock(String sku, String authorizationHeader) {
        return restClient.get()
                .uri("/internal/inventory/stock/{sku}", sku)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .body(Map.class);
    }
}