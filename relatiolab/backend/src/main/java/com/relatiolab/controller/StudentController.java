package com.relatiolab.controller;

import com.relatiolab.dto.request.CreateProfileRequest;
import com.relatiolab.dto.request.CreateStudentRequest;
import com.relatiolab.dto.response.StudentResponse;
import com.relatiolab.service.StudentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody CreateStudentRequest request) {
        return studentService.create(request);
    }

    @GetMapping
    public List<StudentResponse> list() {
        return studentService.list();
    }

    @GetMapping("/{id}")
    public StudentResponse get(@PathVariable Long id) {
        return studentService.get(id);
    }

    @PostMapping("/{id}/profile")
    public StudentResponse upsertProfile(@PathVariable Long id, @Valid @RequestBody CreateProfileRequest request) {
        return studentService.upsertProfile(id, request);
    }

    @DeleteMapping("/{id}/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable Long id) {
        studentService.deleteProfile(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}