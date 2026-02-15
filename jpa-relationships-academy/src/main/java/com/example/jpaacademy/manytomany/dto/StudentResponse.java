package com.example.jpaacademy.manytomany.dto;

import java.util.Set;

public record StudentResponse(
        Long id,
        String name,
        String rollNumber,
        Set<CourseSimple> courses) {
}
