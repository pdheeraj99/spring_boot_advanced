package com.example.jpaacademy.manytomany.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateStudentRequest(
        @NotBlank String name,
        @NotBlank String rollNumber) {
}
