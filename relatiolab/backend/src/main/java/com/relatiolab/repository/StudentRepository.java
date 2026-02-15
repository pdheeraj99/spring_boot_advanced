package com.relatiolab.repository;

import com.relatiolab.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Problem: Lazy loading enrollments in a loop causes N+1 queries.
    // Fix: JOIN FETCH brings student + enrollments in a single SQL.
    // When not to use: If you only need lightweight student rows without child collections.
    @Query("select distinct s from Student s left join fetch s.enrollments e left join fetch e.course")
    List<Student> findAllWithEnrollmentsJoinFetch();

    // Problem: N+1 for profile/enrollment graph when rendering dashboard.
    // Fix: EntityGraph declares fetch plan without long JPQL.
    // When not to use: Dynamic graph requirements that change at runtime frequently.
    @EntityGraph(attributePaths = {"profile", "enrollments", "enrollments.course"})
    @Query("select s from Student s")
    List<Student> findAllWithGraph();

    @EntityGraph(attributePaths = {"profile", "enrollments", "enrollments.course"})
    Optional<Student> findWithGraphById(Long id);
}