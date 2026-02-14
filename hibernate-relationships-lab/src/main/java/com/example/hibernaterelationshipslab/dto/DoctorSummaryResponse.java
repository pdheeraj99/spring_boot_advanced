package com.example.hibernaterelationshipslab.dto;

public record DoctorSummaryResponse(
        Long id,
        String fullName,
        String licenseNumber
) {
}
