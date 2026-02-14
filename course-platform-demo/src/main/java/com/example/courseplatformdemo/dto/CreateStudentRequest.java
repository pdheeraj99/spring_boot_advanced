package com.example.courseplatformdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record CreateStudentRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        String bio,
        String phoneNumber,
        LocalDate dateOfBirth) {
}