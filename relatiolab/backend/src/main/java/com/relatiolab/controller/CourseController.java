package com.relatiolab.controller;

import com.relatiolab.dto.request.CreateCourseRequest;
import com.relatiolab.dto.response.CourseResponse;
import com.relatiolab.service.CourseService;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@Valid @RequestBody CreateCourseRequest request) {
        return courseService.create(request);
    }

    @GetMapping
    public List<CourseResponse> list() {
        return courseService.list();
    }

    @GetMapping("/{id}")
    public CourseResponse get(@PathVariable Long id) {
        return courseService.get(id);
    }

    @PostMapping("/{courseId}/mentors/{mentorId}")
    public CourseResponse linkMentor(@PathVariable Long courseId, @PathVariable Long mentorId) {
        return courseService.linkMentor(courseId, mentorId);
    }

    @DeleteMapping("/{courseId}/mentors/{mentorId}")
    public CourseResponse unlinkMentor(@PathVariable Long courseId, @PathVariable Long mentorId) {
        return courseService.unlinkMentor(courseId, mentorId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}