package com.relatiolab.repository;

import com.relatiolab.entity.Enrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    // Problem: Enrollment report can trigger N+1 for student and course names.
    // Fix: JOIN FETCH loads parent rows eagerly for reporting endpoint.
    // When not to use: Write-heavy endpoint where you don't read associations.
    @Query("select e from Enrollment e join fetch e.student join fetch e.course")
    List<Enrollment> findAllReportJoinFetch();

    @EntityGraph(attributePaths = {"student", "course"})
    @Query("select e from Enrollment e")
    List<Enrollment> findAllWithGraph();

    @EntityGraph(attributePaths = {"student", "course"})
    Optional<Enrollment> findGraphById(Long id);
}