package com.relatiolab.dto.response;

public record FetchComparisonResponse(String scenario, String mode, long queryCount, long selectCount,
                                      long durationMs, Object data) {
}