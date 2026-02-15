package com.example.jpaacademy.onetoone.dto;

public record HusbandResponse(
        Long id,
        String name,
        int age,
        WifeResponse wife) {
}
