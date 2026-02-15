package com.relatiolab.repository;

import com.relatiolab.entity.Mentor;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @EntityGraph(attributePaths = {"courses", "skills"})
    Optional<Mentor> findGraphById(Long id);
}