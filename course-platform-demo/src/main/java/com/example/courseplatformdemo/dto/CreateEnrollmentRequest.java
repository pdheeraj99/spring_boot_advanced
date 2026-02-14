package com.example.courseplatformdemo.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(
        @NotNull Long studentId,
        @NotNull Long courseId) {
}