package com.example.nplus1demo.dto;

import java.time.Instant;

public record OrderAuditResponse(Long id, String action, Instant timestamp, String username) {
}
