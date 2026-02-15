package com.relatiolab.controller;

import com.relatiolab.dto.request.CreateEnrollmentRequest;
import com.relatiolab.dto.request.UpdateProgressRequest;
import com.relatiolab.dto.response.EnrollmentResponse;
import com.relatiolab.service.EnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse create(@Valid @RequestBody CreateEnrollmentRequest request) {
        return enrollmentService.create(request);
    }

    @GetMapping
    public List<EnrollmentResponse> list(@RequestParam(required = false) Long studentId,
                                         @RequestParam(required = false) Long courseId) {
        return enrollmentService.list(studentId, courseId);
    }

    @GetMapping("/{id}")
    public EnrollmentResponse get(@PathVariable Long id) {
        return enrollmentService.get(id);
    }

    @PatchMapping("/{id}/progress")
    public EnrollmentResponse updateProgress(@PathVariable Long id, @Valid @RequestBody UpdateProgressRequest request) {
        return enrollmentService.updateProgress(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        enrollmentService.delete(id);
    }
}