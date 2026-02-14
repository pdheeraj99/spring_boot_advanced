package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.CreateInstructorRequest;
import com.example.courseplatformdemo.dto.InstructorResponse;
import com.example.courseplatformdemo.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
public class InstructorControllerV1 {

    private final InstructorService instructorService;

    @PostMapping
    public InstructorResponse createInstructor(@Valid @RequestBody CreateInstructorRequest request) {
        return instructorService.createInstructor(request);
    }
}