package com.example.courseplatformdemo.dto;

public record InstructorResponse(
        Long id,
        String name,
        String email,
        InstructorProfileResponse profile) {
}