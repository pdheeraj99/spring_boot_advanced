package com.example.hibernaterelationshipslab.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSpecialtyRequest(
        @NotBlank String code,
        @NotBlank String name
) {
}
