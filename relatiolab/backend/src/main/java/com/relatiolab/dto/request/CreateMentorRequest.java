package com.relatiolab.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateMentorRequest(@NotBlank String name, @NotBlank String expertiseLevel) {
}