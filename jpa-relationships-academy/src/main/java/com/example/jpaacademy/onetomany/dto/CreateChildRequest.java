package com.example.jpaacademy.onetomany.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateChildRequest(
        @NotBlank String name,
        @Min(0) int age) {
}
