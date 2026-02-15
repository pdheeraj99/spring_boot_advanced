package com.relatiolab.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(@NotNull Long studentId, @NotNull Long courseId,
                                      @Min(0) @Max(100) Integer progressPercent) {
}