package com.example.courseplatformdemo.controller;

import com.example.courseplatformdemo.dto.QueryComparisonResponse;
import com.example.courseplatformdemo.dto.SmokeSuiteResponse;
import com.example.courseplatformdemo.service.QueryAnalysisService;
import com.example.courseplatformdemo.service.SmokeSuiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final SmokeSuiteService smokeSuiteService;
    private final QueryAnalysisService queryAnalysisService;

    @PostMapping("/run-smoke-suite")
    public SmokeSuiteResponse runSmokeSuite() {
        return smokeSuiteService.run();
    }

    @GetMapping("/compare-courses")
    public QueryComparisonResponse compareCourses() {
        return queryAnalysisService.compareCourseQueries();
    }

    @GetMapping("/compare-dashboard")
    public QueryComparisonResponse compareDashboard() {
        Long studentId = smokeSuiteService.resolveAnyStudentId();
        return queryAnalysisService.compareDashboardQueries(studentId);
    }
}