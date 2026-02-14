package com.example.courseplatformdemo.dto;

import java.time.LocalDate;

public record StudentProfileResponse(
        Long id,
        String bio,
        String phoneNumber,
        LocalDate dateOfBirth) {
}