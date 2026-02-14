package com.example.courseplatformdemo.repository;

import com.example.courseplatformdemo.entity.Instructor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByEmail(String email);
}