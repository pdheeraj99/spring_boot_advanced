package com.relatiolab.controller;

import com.relatiolab.dto.response.FetchComparisonResponse;
import com.relatiolab.dto.response.SqlSummaryResponse;
import com.relatiolab.dto.response.SqlTraceResponse;
import com.relatiolab.service.DebugService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
public class DebugController {

    private final DebugService debugService;

    @GetMapping("/sql/recent")
    public List<SqlTraceResponse> recentSql(@RequestParam(defaultValue = "50") int limit) {
        return debugService.recentSql(limit);
    }

    @GetMapping("/sql/by-request/{requestId}")
    public List<SqlTraceResponse> byRequest(@PathVariable String requestId) {
        return debugService.sqlByRequestId(requestId);
    }

    @GetMapping("/sql/summary")
    public SqlSummaryResponse summary() {
        return debugService.summary();
    }

    @DeleteMapping("/sql/clear")
    public void clear() {
        debugService.clear();
    }

    @GetMapping("/fetch-comparison")
    public FetchComparisonResponse fetchComparison(@RequestParam(defaultValue = "join-fetch") String mode) {
        return debugService.fetchComparison(mode);
    }

    @GetMapping("/nplus1/students-with-enrollments")
    public FetchComparisonResponse studentsNPlusOne(@RequestParam(defaultValue = "bad") String mode) {
        return debugService.nPlusOneStudents(mode);
    }

    @GetMapping("/nplus1/courses-with-mentors")
    public FetchComparisonResponse coursesNPlusOne(@RequestParam(defaultValue = "bad") String mode) {
        return debugService.nPlusOneCourses(mode);
    }

    @GetMapping("/nplus1/enrollment-report")
    public FetchComparisonResponse enrollmentReport(@RequestParam(defaultValue = "bad") String mode) {
        return debugService.nPlusOneEnrollmentReport(mode);
    }
}