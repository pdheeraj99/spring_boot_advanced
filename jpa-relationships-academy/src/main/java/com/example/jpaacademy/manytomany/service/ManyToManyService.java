package com.example.jpaacademy.manytomany.service;

import com.example.jpaacademy.manytomany.dto.*;
import com.example.jpaacademy.manytomany.entity.Course;
import com.example.jpaacademy.manytomany.entity.Student;
import com.example.jpaacademy.manytomany.repo.CourseRepo;
import com.example.jpaacademy.manytomany.repo.StudentRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManyToManyService {

    private final StudentRepo studentRepo;
    private final CourseRepo courseRepo;

    public ManyToManyService(StudentRepo studentRepo, CourseRepo courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        Student student = new Student(request.name(), request.rollNumber());
        Student saved = studentRepo.save(student);
        return toStudentResponse(saved);
    }

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        Course course = new Course(request.name(), request.code());
        Course saved = courseRepo.save(course);
        return toCourseResponse(saved);
    }

    @Transactional
    public StudentResponse enrollStudentInCourse(Long studentId, Long courseId) {
        Student student = studentRepo.findWithCoursesById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        if (student.getCourses().contains(course)) {
            throw new RuntimeException("Student already enrolled in this course!");
        }

        // IMPORTANT: owning side (Student) nunchi add cheyyali!
        student.enrollInCourse(course);
        studentRepo.save(student); // owning side save → join table lo row insert avutundi
        return toStudentResponse(student);
    }

    @Transactional
    public void dropCourse(Long studentId, Long courseId) {
        Student student = studentRepo.findWithCoursesById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        student.dropCourse(course);
        studentRepo.save(student); // owning side save → join table nunchi row delete avutundi
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepo.findAllBy()
                .stream()
                .map(this::toStudentResponse)
                .toList();
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepo.findAllBy()
                .stream()
                .map(this::toCourseResponse)
                .toList();
    }

    private StudentResponse toStudentResponse(Student student) {
        Set<CourseSimple> courses = student.getCourses()
                .stream()
                .map(c -> new CourseSimple(c.getId(), c.getName(), c.getCode()))
                .collect(Collectors.toSet());
        return new StudentResponse(student.getId(), student.getName(), student.getRollNumber(), courses);
    }

    private CourseResponse toCourseResponse(Course course) {
        Set<StudentSimple> students = course.getStudents()
                .stream()
                .map(s -> new StudentSimple(s.getId(), s.getName(), s.getRollNumber()))
                .collect(Collectors.toSet());
        return new CourseResponse(course.getId(), course.getName(), course.getCode(), students);
    }
}
