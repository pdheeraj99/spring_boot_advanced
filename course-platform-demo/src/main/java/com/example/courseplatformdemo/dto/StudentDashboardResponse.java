package com.example.courseplatformdemo.dto;

import java.util.List;

public record StudentDashboardResponse(
        Long studentId,
        String studentName,
        int totalEnrollments,
        long completedCourses,
        double averageProgress,
        List<EnrollmentResponse> enrollments) {
}