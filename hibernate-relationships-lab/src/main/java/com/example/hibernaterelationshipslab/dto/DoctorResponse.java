package com.example.hibernaterelationshipslab.dto;

import java.util.Set;

public record DoctorResponse(
        Long id,
        String fullName,
        String licenseNumber,
        Set<SpecialtyResponse> specialties
) {
}
