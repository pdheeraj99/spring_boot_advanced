package com.example.courseplatformdemo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

public record CreateCourseRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull Boolean published,
        @NotNull Long instructorId,
        Set<String> tagNames,
        String certificateTemplateName,
        String certificateHeaderText,
        String certificateFooterText) {
}