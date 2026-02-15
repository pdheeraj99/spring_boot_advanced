package com.example.jpaacademy.onetomany.dto;

import java.util.List;

public record MotherResponse(
        Long id,
        String name,
        int age,
        List<ChildResponse> children) {
}
