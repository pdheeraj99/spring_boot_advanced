package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.CreateStudentRequest;
import com.example.courseplatformdemo.dto.StudentDashboardResponse;
import com.example.courseplatformdemo.dto.StudentResponse;
import com.example.courseplatformdemo.service.LearningDashboardService;
import com.example.courseplatformdemo.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentControllerV1 {

    private final StudentService studentService;
    private final LearningDashboardService learningDashboardService;

    @PostMapping
    public StudentResponse createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return studentService.createStudent(request);
    }

    @PostMapping("/{studentId}/buddies/{buddyId}")
    public StudentResponse addBuddy(@PathVariable Long studentId, @PathVariable Long buddyId) {
        return studentService.addBuddy(studentId, buddyId);
    }

    @GetMapping("/{studentId}/dashboard")
    public StudentDashboardResponse getDashboard(@PathVariable Long studentId) {
        return learningDashboardService.getDashboardV1(studentId);
    }
}