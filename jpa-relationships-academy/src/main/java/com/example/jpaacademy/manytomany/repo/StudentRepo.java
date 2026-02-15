package com.example.jpaacademy.manytomany.repo;

import com.example.jpaacademy.manytomany.entity.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = { "courses" })
    List<Student> findAllBy();

    @EntityGraph(attributePaths = { "courses" })
    Optional<Student> findWithCoursesById(Long id);
}
