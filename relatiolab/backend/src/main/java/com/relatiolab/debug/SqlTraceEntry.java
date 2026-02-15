package com.relatiolab.debug;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SqlTraceEntry {
    private final LocalDateTime timestamp;
    private final String requestId;
    private final String method;
    private final String path;
    private final String operation;
    private final String sql;
}