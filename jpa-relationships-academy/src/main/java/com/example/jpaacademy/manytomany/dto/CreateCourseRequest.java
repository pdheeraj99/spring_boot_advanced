package com.example.jpaacademy.manytomany.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCourseRequest(
        @NotBlank String name,
        @NotBlank String code) {
}
