package com.example.hibernaterelationshipslab.dto;

import com.example.hibernaterelationshipslab.entity.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        LocalDateTime appointmentAt,
        AppointmentStatus status,
        String reason,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName
) {
}
