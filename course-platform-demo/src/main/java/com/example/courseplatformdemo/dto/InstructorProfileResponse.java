package com.example.courseplatformdemo.dto;

public record InstructorProfileResponse(
        Long id,
        String headline,
        String expertise,
        Integer yearsExperience) {
}