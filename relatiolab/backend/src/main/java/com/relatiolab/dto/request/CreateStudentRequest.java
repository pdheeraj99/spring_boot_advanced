package com.relatiolab.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateStudentRequest(@NotBlank String name, @Email @NotBlank String email) {
}