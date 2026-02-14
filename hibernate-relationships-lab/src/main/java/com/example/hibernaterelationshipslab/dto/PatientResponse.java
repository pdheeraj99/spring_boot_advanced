package com.example.hibernaterelationshipslab.dto;

import java.util.List;

public record PatientResponse(
        Long id,
        String fullName,
        String email,
        InsuranceCardResponse insuranceCard,
        List<AppointmentResponse> appointments
) {
}
