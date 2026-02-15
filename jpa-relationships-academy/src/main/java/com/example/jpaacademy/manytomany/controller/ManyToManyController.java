package com.example.jpaacademy.manytomany.controller;

import com.example.jpaacademy.manytomany.dto.*;
import com.example.jpaacademy.manytomany.service.ManyToManyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manytomany")
public class ManyToManyController {

    private final ManyToManyService service;

    public ManyToManyController(ManyToManyService service) {
        this.service = service;
    }

    @PostMapping("/students")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudent(request));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCourse(request));
    }

    @PostMapping("/students/{studentId}/courses/{courseId}")
    public ResponseEntity<StudentResponse> enrollInCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(service.enrollStudentInCourse(studentId, courseId));
    }

    @DeleteMapping("/students/{studentId}/courses/{courseId}")
    public ResponseEntity<Void> dropCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        service.dropCourse(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents());
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(service.getAllCourses());
    }
}
