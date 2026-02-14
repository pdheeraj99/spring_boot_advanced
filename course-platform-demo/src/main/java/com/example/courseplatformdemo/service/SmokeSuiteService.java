package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.CreateCourseRequest;
import com.example.courseplatformdemo.dto.CreateEnrollmentRequest;
import com.example.courseplatformdemo.dto.CreateInstructorRequest;
import com.example.courseplatformdemo.dto.CreateLessonRequest;
import com.example.courseplatformdemo.dto.CreateStudentRequest;
import com.example.courseplatformdemo.dto.EnrollmentProgressUpdateRequest;
import com.example.courseplatformdemo.dto.QueryComparisonResponse;
import com.example.courseplatformdemo.dto.SmokeSuiteResponse;
import com.example.courseplatformdemo.entity.Student;
import com.example.courseplatformdemo.repository.StudentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SmokeSuiteService {

    private final StudentService studentService;
    private final InstructorService instructorService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final LearningDashboardService learningDashboardService;
    private final QueryAnalysisService queryAnalysisService;
    private final StudentRepository studentRepository;

    @Transactional
    public SmokeSuiteResponse run() {
        String suffix = String.valueOf(System.currentTimeMillis());
        var student = studentService.createStudent(new CreateStudentRequest(
                "Smoke Student", "smoke.student." + suffix + "@example.com", "smoke", "9000000000", LocalDate.of(1999, 1, 1)));

        var buddy = studentService.createStudent(new CreateStudentRequest(
                "Smoke Buddy", "smoke.buddy." + suffix + "@example.com", "buddy", "9111111111", LocalDate.of(1998, 1, 1)));

        studentService.addBuddy(student.id(), buddy.id());

        var instructor = instructorService.createInstructor(new CreateInstructorRequest(
                "Smoke Instructor", "smoke.instructor." + suffix + "@example.com", "Senior Trainer", "Spring", 8));

        var course = courseService.createCourse(new CreateCourseRequest(
                "Smoke Spring Course",
                "End to end smoke",
                BigDecimal.valueOf(99.0),
                true,
                instructor.id(),
                Set.of("Spring", "JPA"),
                "Default Template",
                "Congrats",
                "Keep learning"));

        courseService.addLesson(course.id(), new CreateLessonRequest("Intro", "https://videos/1", 10, 1));

        var enrollment = enrollmentService.createEnrollment(new CreateEnrollmentRequest(student.id(), course.id()));
        enrollmentService.updateProgress(enrollment.id(), new EnrollmentProgressUpdateRequest(100));

        learningDashboardService.getDashboardV1(student.id());
        learningDashboardService.getDashboardV2(student.id());

        QueryComparisonResponse comparison = queryAnalysisService.compareCourseQueries();

        return new SmokeSuiteResponse(true, "Smoke suite executed successfully", comparison);
    }

    public Long resolveAnyStudentId() {
        return studentRepository.findAll().stream().map(Student::getId).findFirst().orElseThrow();
    }
}
