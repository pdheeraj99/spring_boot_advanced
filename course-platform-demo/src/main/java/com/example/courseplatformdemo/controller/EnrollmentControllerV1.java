package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.CreateEnrollmentRequest;
import com.example.courseplatformdemo.dto.EnrollmentProgressUpdateRequest;
import com.example.courseplatformdemo.dto.EnrollmentResponse;
import com.example.courseplatformdemo.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentControllerV1 {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public EnrollmentResponse createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        return enrollmentService.createEnrollment(request);
    }

    @PatchMapping("/{id}/progress")
    public EnrollmentResponse updateProgress(@PathVariable Long id,
            @Valid @RequestBody EnrollmentProgressUpdateRequest request) {
        return enrollmentService.updateProgress(id, request);
    }
}