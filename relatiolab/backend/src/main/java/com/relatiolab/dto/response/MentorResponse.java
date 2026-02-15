package com.relatiolab.dto.response;

import java.util.Set;

public record MentorResponse(Long id, String name, String expertiseLevel,
                             Set<CourseSimpleResponse> courses,
                             Set<SkillResponse> skills) {
}