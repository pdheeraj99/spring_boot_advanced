package com.relatiolab.debug;

public final class RequestTraceContext {
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> METHOD = new ThreadLocal<>();
    private static final ThreadLocal<String> PATH = new ThreadLocal<>();

    private RequestTraceContext() {
    }

    public static void set(String requestId, String method, String path) {
        REQUEST_ID.set(requestId);
        METHOD.set(method);
        PATH.set(path);
    }

    public static String requestId() {
        return REQUEST_ID.get();
    }

    public static String method() {
        return METHOD.get();
    }

    public static String path() {
        return PATH.get();
    }

    public static void clear() {
        REQUEST_ID.remove();
        METHOD.remove();
        PATH.remove();
    }
}