package com.example.nplus1demo.metrics;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.slf4j.MDC;

public class RequestAwareP6SpyFormatter implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId,
                                String now,
                                long elapsed,
                                String category,
                                String prepared,
                                String sql,
                                String url) {
        String requestId = MDC.get("requestId");
        if (requestId != null && sql != null && !sql.isBlank() && "statement".equalsIgnoreCase(category)) {
            RequestMetricsStore store = RequestMetricsStore.getInstance();
            if (store != null) {
                store.incrementSql(requestId);
            }
        }
        String cleanSql = sql == null ? "" : sql.replaceAll("\\s+", " ").trim();
        return String.format("req=%s|elapsedMs=%d|category=%s|sql=%s",
                requestId == null ? "n/a" : requestId,
                elapsed,
                category,
                cleanSql);
    }
}
