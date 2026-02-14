package com.example.courseplatformdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.courseplatformdemo.entity.Course;
import com.example.courseplatformdemo.entity.Enrollment;
import com.example.courseplatformdemo.entity.Student;
import com.example.courseplatformdemo.repository.CourseRepository;
import com.example.courseplatformdemo.repository.EnrollmentRepository;
import com.example.courseplatformdemo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class RelationshipMappingTests {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    @Transactional
    void oneToOneProfileAndBuddyRelationshipWork() {
        Student a = studentRepository.findByEmail("student1@example.com").orElseThrow();
        Student b = studentRepository.findByEmail("student2@example.com").orElseThrow();

        a.addBuddy(b);

        assertThat(a.getProfile()).isNotNull();
        assertThat(a.getStudyBuddies()).extracting(Student::getId).contains(b.getId());
        assertThat(b.getStudyBuddies()).extracting(Student::getId).contains(a.getId());
    }

    @Test
    @Transactional
    void courseTagsLessonsAndEnrollmentMappingsWork() {
        Course course = courseRepository.findAll().stream().findFirst().orElseThrow();

        assertThat(course.getTags()).isNotEmpty();
        assertThat(course.getLessons()).isNotEmpty();

        Enrollment enrollment = enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse().getId().equals(course.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(enrollment.getStudent()).isNotNull();
        assertThat(enrollment.getCourse()).isNotNull();
    }
}