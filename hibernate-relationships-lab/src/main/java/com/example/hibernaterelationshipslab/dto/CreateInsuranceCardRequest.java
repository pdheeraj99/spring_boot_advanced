package com.example.hibernaterelationshipslab.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateInsuranceCardRequest(
        @NotBlank String providerName,
        @NotBlank String policyNumber,
        @NotNull @Future LocalDate validTill
) {
}
