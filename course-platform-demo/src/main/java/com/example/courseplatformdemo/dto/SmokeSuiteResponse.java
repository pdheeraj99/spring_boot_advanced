package com.example.courseplatformdemo.dto;

public record SmokeSuiteResponse(
        boolean success,
        String message,
        QueryComparisonResponse queryComparison) {
}