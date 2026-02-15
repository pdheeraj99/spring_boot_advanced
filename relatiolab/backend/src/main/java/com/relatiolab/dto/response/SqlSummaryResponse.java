package com.relatiolab.dto.response;

public record SqlSummaryResponse(long total, long selectCount, long insertCount, long updateCount, long deleteCount) {
}