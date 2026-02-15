package com.example.jpaacademy.manytomany.repo;

import com.example.jpaacademy.manytomany.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {

    @EntityGraph(attributePaths = { "students" })
    List<Course> findAllBy();

    @EntityGraph(attributePaths = { "students" })
    Optional<Course> findWithStudentsById(Long id);
}
