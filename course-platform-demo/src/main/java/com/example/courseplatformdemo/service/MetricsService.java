package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.QueryComparisonResponse;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private volatile QueryComparisonResponse latest = new QueryComparisonResponse(0, 0, 0.0);

    public void setLatest(QueryComparisonResponse latest) {
        this.latest = latest;
    }

    public QueryComparisonResponse getLatest() {
        return latest;
    }
}