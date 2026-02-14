package com.example.hibernaterelationshipslab.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePatientRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email
) {
}
