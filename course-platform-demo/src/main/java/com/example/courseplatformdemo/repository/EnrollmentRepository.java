package com.example.courseplatformdemo.repository;

import com.example.courseplatformdemo.entity.Enrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    @Query("SELECT DISTINCT e FROM Enrollment e " +
            "JOIN FETCH e.course c " +
            "JOIN FETCH c.instructor " +
            "LEFT JOIN FETCH c.tags " +
            "LEFT JOIN FETCH c.certificateTemplate " +
            "WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentIdWithCourseDetails(@Param("studentId") Long studentId);

    @Query("SELECT DISTINCT e FROM Enrollment e JOIN FETCH e.course c LEFT JOIN FETCH c.lessons WHERE e.id IN :ids")
    List<Enrollment> fetchLessonsForEnrollments(@Param("ids") List<Long> ids);
}