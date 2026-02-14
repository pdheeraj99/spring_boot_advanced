package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.CreateEnrollmentRequest;
import com.example.courseplatformdemo.dto.EnrollmentProgressUpdateRequest;
import com.example.courseplatformdemo.dto.EnrollmentResponse;
import com.example.courseplatformdemo.entity.Course;
import com.example.courseplatformdemo.entity.Enrollment;
import com.example.courseplatformdemo.entity.Student;
import com.example.courseplatformdemo.exception.ConflictException;
import com.example.courseplatformdemo.exception.ResourceNotFoundException;
import com.example.courseplatformdemo.repository.EnrollmentRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final CourseService courseService;
    private final DtoMapper dtoMapper;

    @Transactional
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        enrollmentRepository.findByStudentIdAndCourseId(request.studentId(), request.courseId()).ifPresent(existing -> {
            throw new ConflictException("Student already enrolled in this course");
        });

        Student student = studentService.getStudentEntity(request.studentId());
        Course course = courseService.getCourseEntity(request.courseId());

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setProgressPercent(0);
        enrollment.setCompleted(false);
        enrollment.setPricePaid(course.getPrice());

        return dtoMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, EnrollmentProgressUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));
        int progress = request.progressPercent();
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        enrollment.setProgressPercent(progress);
        enrollment.setCompleted(progress == 100);
        return dtoMapper.toEnrollmentResponse(enrollment);
    }
}