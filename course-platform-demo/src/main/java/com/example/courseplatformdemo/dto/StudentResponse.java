package com.example.courseplatformdemo.dto;

import java.util.List;

public record StudentResponse(
        Long id,
        String name,
        String email,
        StudentProfileResponse profile,
        List<Long> buddyIds) {
}