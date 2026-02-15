package com.relatiolab.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateProfileRequest(@NotBlank String phone, String address, String linkedinUrl) {
}