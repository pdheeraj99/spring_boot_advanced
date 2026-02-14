package com.demo.circuitbreaker.model;

public enum ResponseSource {
    EXTERNAL_SERVICE,
    REDIS_CACHE,
    FALLBACK_ERROR
}
