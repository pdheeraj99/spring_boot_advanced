package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.StudentDashboardResponse;
import com.example.courseplatformdemo.service.LearningDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/students")
@RequiredArgsConstructor
public class StudentControllerV2 {

    private final LearningDashboardService learningDashboardService;

    @GetMapping("/{studentId}/dashboard/optimized")
    public StudentDashboardResponse getOptimizedDashboard(@PathVariable Long studentId) {
        return learningDashboardService.getDashboardV2(studentId);
    }
}