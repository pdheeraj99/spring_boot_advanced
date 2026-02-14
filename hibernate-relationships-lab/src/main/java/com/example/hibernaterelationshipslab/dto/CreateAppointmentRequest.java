package com.example.hibernaterelationshipslab.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        @NotNull Long doctorId,
        @NotNull Long patientId,
        @NotNull @Future LocalDateTime appointmentAt,
        @NotBlank String reason
) {
}
