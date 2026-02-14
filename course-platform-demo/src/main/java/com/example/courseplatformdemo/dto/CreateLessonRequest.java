package com.example.courseplatformdemo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateLessonRequest(
        @NotBlank String title,
        @NotBlank String videoUrl,
        @Min(1) Integer durationMinutes,
        @Min(1) Integer sortOrder) {
}