package com.example.courseplatformdemo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseTitle,
        Integer lessonCount,
        Set<String> tagNames,
        LocalDateTime enrolledAt,
        Integer progressPercent,
        Boolean completed,
        BigDecimal pricePaid) {
}
