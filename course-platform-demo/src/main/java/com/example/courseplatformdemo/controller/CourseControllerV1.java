package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.CreateCourseRequest;
import com.example.courseplatformdemo.dto.CreateLessonRequest;
import com.example.courseplatformdemo.dto.CourseResponse;
import com.example.courseplatformdemo.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseControllerV1 {

    private final CourseService courseService;

    @GetMapping("/courses")
    public List<CourseResponse> getCourses() {
        return courseService.getAllCoursesV1();
    }

    @PostMapping("/courses")
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return courseService.createCourse(request);
    }

    @PostMapping("/courses/{courseId}/lessons")
    public CourseResponse addLesson(@PathVariable Long courseId, @Valid @RequestBody CreateLessonRequest request) {
        return courseService.addLesson(courseId, request);
    }

    @GetMapping("/tags/{tagName}/courses")
    public List<CourseResponse> getCoursesByTag(@PathVariable String tagName) {
        return courseService.getCoursesByTag(tagName);
    }
}