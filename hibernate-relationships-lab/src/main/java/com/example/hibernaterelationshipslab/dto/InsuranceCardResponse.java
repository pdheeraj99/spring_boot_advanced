package com.example.hibernaterelationshipslab.dto;

import java.time.LocalDate;

public record InsuranceCardResponse(
        Long id,
        String providerName,
        String policyNumber,
        LocalDate validTill
) {
}
