package com.example.courseplatformdemo.dto;

public record LessonResponse(
        Long id,
        String title,
        String videoUrl,
        Integer durationMinutes,
        Integer sortOrder) {
}