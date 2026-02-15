package com.relatiolab.debug;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SqlTraceStore {

    private static final int MAX_SIZE = 1000;
    private final Deque<SqlTraceEntry> traces = new ArrayDeque<>();

    public synchronized void add(SqlTraceEntry entry) {
        if (traces.size() >= MAX_SIZE) {
            traces.removeFirst();
        }
        traces.addLast(entry);
    }

    public synchronized List<SqlTraceEntry> recent(int limit) {
        List<SqlTraceEntry> all = new ArrayList<>(traces);
        int from = Math.max(0, all.size() - limit);
        return all.subList(from, all.size());
    }

    public synchronized List<SqlTraceEntry> byRequestId(String requestId) {
        return traces.stream().filter(t -> requestId.equals(t.getRequestId())).toList();
    }

    public synchronized List<SqlTraceEntry> snapshot() {
        return new ArrayList<>(traces);
    }

    public synchronized void clear() {
        traces.clear();
    }
}