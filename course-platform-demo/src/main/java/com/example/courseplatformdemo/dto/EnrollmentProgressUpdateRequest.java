package com.example.courseplatformdemo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EnrollmentProgressUpdateRequest(
        @NotNull @Min(0) @Max(100) Integer progressPercent) {
}