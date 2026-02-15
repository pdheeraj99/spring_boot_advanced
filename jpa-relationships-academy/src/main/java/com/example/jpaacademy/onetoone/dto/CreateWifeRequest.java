package com.example.jpaacademy.onetoone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWifeRequest(
        @NotBlank String name,
        @Min(18) int age,
        @NotNull Long husbandId) {
}
