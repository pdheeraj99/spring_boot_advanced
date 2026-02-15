package com.example.jpaacademy.manytomany.dto;

import java.util.Set;

public record CourseResponse(
        Long id,
        String name,
        String code,
        Set<StudentSimple> students) {
}
