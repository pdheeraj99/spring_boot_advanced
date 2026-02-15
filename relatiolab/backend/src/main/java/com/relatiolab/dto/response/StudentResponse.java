package com.relatiolab.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record StudentResponse(Long id, String name, String email, LocalDateTime createdAt,
                              StudentProfileResponse profile, List<EnrollmentResponse> enrollments) {
}