package com.example.jpaacademy.onetoone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateHusbandRequest(
        @NotBlank String name,
        @Min(18) int age) {
}
