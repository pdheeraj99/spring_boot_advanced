package com.relatiolab;

import static org.assertj.core.api.Assertions.assertThat;

import com.relatiolab.entity.Enrollment;
import com.relatiolab.entity.Course;
import com.relatiolab.entity.Student;
import com.relatiolab.entity.StudentProfile;
import com.relatiolab.repository.CourseRepository;
import com.relatiolab.repository.EnrollmentRepository;
import com.relatiolab.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RelationshipMappingDataJpaTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void oneToOneOrphanRemovalShouldDeleteProfileRow() {
        Student student = new Student();
        student.setName("A");
        student.setEmail("a@a.com");
        StudentProfile profile = new StudentProfile();
        profile.setPhone("9999999999");
        student.setProfile(profile);
        student = studentRepository.save(student);
        Long profileId = student.getProfile().getId();

        student.setProfile(null);
        studentRepository.saveAndFlush(student);
        entityManager.clear();

        assertThat(entityManager.find(StudentProfile.class, profileId)).isNull();
    }

    @Test
    void oneToManyOrphanRemovalShouldDeleteEnrollment() {
        Student student = new Student();
        student.setName("B");
        student.setEmail("b@a.com");
        Course course = new Course();
        course.setTitle("JPA basics");
        course.setFee(java.math.BigDecimal.valueOf(1000));
        courseRepository.saveAndFlush(course);
        Enrollment enrollment = new Enrollment();
        student.addEnrollment(enrollment);
        enrollment.setCourse(course);
        studentRepository.saveAndFlush(student);

        Long enrollmentId = student.getEnrollments().getFirst().getId();
        student.removeEnrollment(student.getEnrollments().getFirst());
        studentRepository.saveAndFlush(student);

        assertThat(enrollmentRepository.existsById(enrollmentId)).isFalse();
    }
}
