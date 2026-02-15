package com.relatiolab.repository;

import com.relatiolab.entity.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Problem: Course list + mentors can trigger N+1 selects.
    // Fix: JOIN FETCH loads mentors in one query.
    // When not to use: Large result sets where joined rows explode quickly.
    @Query("select distinct c from Course c left join fetch c.mentors")
    List<Course> findAllWithMentorsJoinFetch();

    @EntityGraph(attributePaths = {"mentors"})
    @Query("select c from Course c")
    List<Course> findAllWithMentorsGraph();

    @EntityGraph(attributePaths = {"mentors"})
    Optional<Course> findWithMentorsById(Long id);
}