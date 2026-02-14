package com.example.courseplatformdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.courseplatformdemo.dto.QueryComparisonResponse;
import com.example.courseplatformdemo.service.QueryAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QueryBehaviorTests {

    @Autowired
    private QueryAnalysisService queryAnalysisService;

    @Test
    void optimizedCoursePathUsesLessOrEqualQueriesThanV1() {
        QueryComparisonResponse response = queryAnalysisService.compareCourseQueries();
        assertThat(response.v2Queries()).isLessThanOrEqualTo(response.v1Queries());
    }

    @Test
    void optimizedDashboardPathUsesLessOrEqualQueriesThanV1() {
        QueryComparisonResponse response = queryAnalysisService.compareDashboardQueries(1L);
        assertThat(response.v2Queries()).isLessThanOrEqualTo(response.v1Queries());
    }
}