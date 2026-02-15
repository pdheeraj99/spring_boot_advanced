package com.relatiolab.dto.response;

import java.math.BigDecimal;
import java.util.Set;

public record CourseResponse(Long id, String title, BigDecimal fee, Boolean active, Set<MentorSimpleResponse> mentors) {
}