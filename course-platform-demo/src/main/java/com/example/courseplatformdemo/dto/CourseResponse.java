package com.example.courseplatformdemo.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record CourseResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        Boolean published,
        Long instructorId,
        String instructorName,
        List<LessonResponse> lessons,
        Set<TagResponse> tags,
        CertificateTemplateResponse certificateTemplate) {
}