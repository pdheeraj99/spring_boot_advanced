package com.relatiolab.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(LocalDateTime timestamp, int status, String code, String message, String path,
                               List<String> details) {
}