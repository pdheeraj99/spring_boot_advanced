package com.relatiolab.dto.response;

import com.relatiolab.entity.EnrollmentStatus;
import java.time.LocalDateTime;

public record EnrollmentResponse(Long id, Long studentId, String studentName, Long courseId, String courseTitle,
                                 Integer progressPercent, EnrollmentStatus status, LocalDateTime enrolledAt) {
}