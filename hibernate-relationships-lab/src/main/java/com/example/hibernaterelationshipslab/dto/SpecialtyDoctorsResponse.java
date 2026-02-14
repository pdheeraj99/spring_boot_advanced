package com.example.hibernaterelationshipslab.dto;

import java.util.List;

public record SpecialtyDoctorsResponse(
        Long id,
        String code,
        String name,
        List<DoctorSummaryResponse> doctors
) {
}
