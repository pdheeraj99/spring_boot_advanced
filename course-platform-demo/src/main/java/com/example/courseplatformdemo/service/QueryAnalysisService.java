package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.QueryComparisonResponse;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryAnalysisService {

    private final CourseService courseService;
    private final LearningDashboardService learningDashboardService;
    private final MetricsService metricsService;
    private final EntityManagerFactory entityManagerFactory;

    public QueryComparisonResponse compareCourseQueries() {
        Statistics stats = getStats();
        stats.clear();
        courseService.getAllCoursesV1();
        long v1 = stats.getPrepareStatementCount();

        stats.clear();
        courseService.getAllCoursesV2();
        long v2 = stats.getPrepareStatementCount();

        QueryComparisonResponse response = buildResponse(v1, v2);
        metricsService.setLatest(response);
        return response;
    }

    public QueryComparisonResponse compareDashboardQueries(Long studentId) {
        Statistics stats = getStats();
        stats.clear();
        learningDashboardService.getDashboardV1(studentId);
        long v1 = stats.getPrepareStatementCount();

        stats.clear();
        learningDashboardService.getDashboardV2(studentId);
        long v2 = stats.getPrepareStatementCount();

        QueryComparisonResponse response = buildResponse(v1, v2);
        metricsService.setLatest(response);
        return response;
    }

    private QueryComparisonResponse buildResponse(long v1, long v2) {
        double reduction = v1 == 0 ? 0.0 : ((double) (v1 - v2) / v1) * 100.0;
        return new QueryComparisonResponse(v1, v2, reduction);
    }

    private Statistics getStats() {
        return entityManagerFactory.unwrap(org.hibernate.SessionFactory.class).getStatistics();
    }
}