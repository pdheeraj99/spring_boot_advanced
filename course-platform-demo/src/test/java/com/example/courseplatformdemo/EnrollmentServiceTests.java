package com.example.courseplatformdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.courseplatformdemo.dto.CreateEnrollmentRequest;
import com.example.courseplatformdemo.dto.EnrollmentProgressUpdateRequest;
import com.example.courseplatformdemo.exception.ConflictException;
import com.example.courseplatformdemo.repository.CourseRepository;
import com.example.courseplatformdemo.repository.StudentRepository;
import com.example.courseplatformdemo.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EnrollmentServiceTests {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void duplicateEnrollmentThrowsConflict() {
        Long studentId = studentRepository.findAll().getFirst().getId();
        Long courseId = courseRepository.findAll().getFirst().getId();

        enrollmentService.createEnrollment(new CreateEnrollmentRequest(studentId, courseId));

        assertThatThrownBy(() -> enrollmentService.createEnrollment(new CreateEnrollmentRequest(studentId, courseId)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void completionAutoTrueAtHundred() {
        Long studentId = studentRepository.findAll().get(1).getId();
        Long courseId = courseRepository.findAll().get(1).getId();

        var enrollment = enrollmentService.createEnrollment(new CreateEnrollmentRequest(studentId, courseId));
        var updated = enrollmentService.updateProgress(enrollment.id(), new EnrollmentProgressUpdateRequest(100));

        assertThat(updated.completed()).isTrue();
        assertThat(updated.progressPercent()).isEqualTo(100);
    }
}