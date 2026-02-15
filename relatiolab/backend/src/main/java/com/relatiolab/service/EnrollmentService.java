package com.relatiolab.service;

import com.relatiolab.dto.request.CreateEnrollmentRequest;
import com.relatiolab.dto.request.UpdateProgressRequest;
import com.relatiolab.dto.response.EnrollmentResponse;
import com.relatiolab.entity.Course;
import com.relatiolab.entity.Enrollment;
import com.relatiolab.entity.EnrollmentStatus;
import com.relatiolab.entity.Student;
import com.relatiolab.exception.ConflictException;
import com.relatiolab.exception.ResourceNotFoundException;
import com.relatiolab.repository.CourseRepository;
import com.relatiolab.repository.EnrollmentRepository;
import com.relatiolab.repository.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public EnrollmentResponse create(CreateEnrollmentRequest request) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(request.studentId(), request.courseId())) {
            throw new ConflictException("Student already enrolled in this course");
        }
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.studentId()));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.courseId()));

        Enrollment enrollment = new Enrollment();
        enrollment.setProgressPercent(request.progressPercent() == null ? 0 : request.progressPercent());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        student.addEnrollment(enrollment);
        enrollment.setCourse(course);
        return dtoMapper.toEnrollment(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, UpdateProgressRequest request) {
        Enrollment enrollment = enrollmentRepository.findGraphById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));
        enrollment.setProgressPercent(request.progressPercent());
        if (request.progressPercent() == 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }
        return dtoMapper.toEnrollment(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public List<EnrollmentResponse> list(Long studentId, Long courseId) {
        List<Enrollment> enrollments;
        if (studentId != null) {
            enrollments = enrollmentRepository.findByStudentId(studentId);
        } else if (courseId != null) {
            enrollments = enrollmentRepository.findByCourseId(courseId);
        } else {
            enrollments = enrollmentRepository.findAllWithGraph();
        }
        return enrollments.stream().map(dtoMapper::toEnrollment).toList();
    }

    @Transactional
    public EnrollmentResponse get(Long id) {
        Enrollment enrollment = enrollmentRepository.findGraphById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + id));
        return dtoMapper.toEnrollment(enrollment);
    }

    public void delete(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrollment not found: " + id);
        }
        enrollmentRepository.deleteById(id);
    }
}