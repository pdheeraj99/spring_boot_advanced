package com.example.courseplatformdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateInstructorRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        String headline,
        String expertise,
        @Min(0) Integer yearsExperience) {
}