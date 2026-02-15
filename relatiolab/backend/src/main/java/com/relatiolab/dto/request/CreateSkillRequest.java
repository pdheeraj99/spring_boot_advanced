package com.relatiolab.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSkillRequest(@NotBlank String code, @NotBlank String displayName) {
}