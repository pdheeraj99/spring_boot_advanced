package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.CourseResponse;
import com.example.courseplatformdemo.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/courses")
@RequiredArgsConstructor
public class CourseControllerV2 {

    private final CourseService courseService;

    @GetMapping("/optimized")
    public List<CourseResponse> getOptimizedCourses() {
        return courseService.getAllCoursesV2();
    }
}