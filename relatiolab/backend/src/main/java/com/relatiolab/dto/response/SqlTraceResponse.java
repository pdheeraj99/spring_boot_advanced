package com.relatiolab.dto.response;

import java.time.LocalDateTime;

public record SqlTraceResponse(LocalDateTime timestamp, String requestId, String method, String path,
                               String operation, String sql) {
}