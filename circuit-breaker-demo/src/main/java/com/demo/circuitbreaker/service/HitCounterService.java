package com.demo.circuitbreaker.service;

public interface HitCounterService {

    void recordHit(String counterKey);

    long getHits(String counterKey);
}
