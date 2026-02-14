package com.example.nplus1demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutionLogService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void logStep(String step) {
        log.info("[{}] {}", LocalDateTime.now().format(FORMATTER), step);
    }
}
