package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.EnrollmentResponse;
import com.example.courseplatformdemo.dto.StudentDashboardResponse;
import com.example.courseplatformdemo.entity.Enrollment;
import com.example.courseplatformdemo.entity.Student;
import com.example.courseplatformdemo.repository.EnrollmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LearningDashboardService {

    private final StudentService studentService;
    private final EnrollmentRepository enrollmentRepository;
    private final DtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public StudentDashboardResponse getDashboardV1(Long studentId) {
        Student student = studentService.getStudentEntity(studentId);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<EnrollmentResponse> responses = enrollments.stream().map(dtoMapper::toEnrollmentResponse).toList();
        return buildDashboard(student, responses);
    }

    @Transactional(readOnly = true)
    public StudentDashboardResponse getDashboardV2(Long studentId) {
        Student student = studentService.getStudentEntity(studentId);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdWithCourseDetails(studentId);
        List<Long> ids = enrollments.stream().map(Enrollment::getId).toList();
        if (!ids.isEmpty()) {
            enrollmentRepository.fetchLessonsForEnrollments(ids);
        }
        List<EnrollmentResponse> responses = enrollments.stream().map(dtoMapper::toEnrollmentResponse).toList();
        return buildDashboard(student, responses);
    }

    private StudentDashboardResponse buildDashboard(Student student, List<EnrollmentResponse> enrollments) {
        int total = enrollments.size();
        long completed = enrollments.stream().filter(EnrollmentResponse::completed).count();
        double average = enrollments.stream().mapToInt(EnrollmentResponse::progressPercent).average().orElse(0.0);
        return new StudentDashboardResponse(student.getId(), student.getName(), total, completed, average, enrollments);
    }
}