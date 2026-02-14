package com.example.courseplatformdemo.repository;

import com.example.courseplatformdemo.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.instructor " +
            "LEFT JOIN FETCH c.tags " +
            "LEFT JOIN FETCH c.certificateTemplate")
    List<Course> findAllDetailedWithJoinFetch();

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.lessons WHERE c.id IN :courseIds")
    List<Course> fetchLessonsForCourses(@Param("courseIds") List<Long> courseIds);

    @Query("SELECT DISTINCT c FROM Course c JOIN c.tags t WHERE LOWER(t.name) = LOWER(:tagName)")
    List<Course> findByTagName(@Param("tagName") String tagName);
}
