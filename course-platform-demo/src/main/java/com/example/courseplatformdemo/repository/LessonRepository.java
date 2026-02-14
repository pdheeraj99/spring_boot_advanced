package com.example.courseplatformdemo.repository;

import com.example.courseplatformdemo.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}