package com.example.courseplatformdemo.dto;

public record QueryComparisonResponse(
        long v1Queries,
        long v2Queries,
        double reductionPercent) {
}