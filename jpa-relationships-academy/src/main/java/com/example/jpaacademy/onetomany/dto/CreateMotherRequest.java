package com.example.jpaacademy.onetomany.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateMotherRequest(
        @NotBlank String name,
        @Min(18) int age) {
}
