package com.relatiolab.debug;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.hibernate.resource.jdbc.spi.StatementInspector;

@RequiredArgsConstructor
public class HibernateSqlInspector implements StatementInspector {

    private final SqlTraceStore sqlTraceStore;

    @Override
    public String inspect(String sql) {
        if (sql == null || sql.isBlank()) {
            return sql;
        }
        String normalized = sql.strip().toLowerCase();
        String op = normalized.startsWith("select") ? "SELECT"
                : normalized.startsWith("insert") ? "INSERT"
                : normalized.startsWith("update") ? "UPDATE"
                : normalized.startsWith("delete") ? "DELETE" : "OTHER";

        sqlTraceStore.add(SqlTraceEntry.builder()
                .timestamp(LocalDateTime.now())
                .requestId(RequestTraceContext.requestId())
                .method(RequestTraceContext.method())
                .path(RequestTraceContext.path())
                .operation(op)
                .sql(sql)
                .build());
        return sql;
    }
}