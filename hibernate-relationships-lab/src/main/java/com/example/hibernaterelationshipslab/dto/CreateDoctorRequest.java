package com.example.hibernaterelationshipslab.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDoctorRequest(
        @NotBlank String fullName,
        @NotBlank String licenseNumber
) {
}
