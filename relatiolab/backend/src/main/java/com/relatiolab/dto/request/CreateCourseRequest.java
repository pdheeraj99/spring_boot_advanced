package com.relatiolab.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateCourseRequest(@NotBlank String title, @DecimalMin("0.0") BigDecimal fee, Boolean active) {
}