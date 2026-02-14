package com.example.nplus1demo.metrics;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.MDC;

public class SqlCountingStatementInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        String requestId = MDC.get("requestId");
        if (requestId != null && sql != null && !sql.isBlank()) {
            RequestMetricsStore store = RequestMetricsStore.getInstance();
            if (store != null) {
                store.incrementSql(requestId);
            }
        }
        return sql;
    }
}
