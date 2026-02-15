package com.relatiolab.service;

import com.relatiolab.dto.response.CourseResponse;
import com.relatiolab.dto.response.CourseSimpleResponse;
import com.relatiolab.dto.response.EnrollmentResponse;
import com.relatiolab.dto.response.MentorResponse;
import com.relatiolab.dto.response.MentorSimpleResponse;
import com.relatiolab.dto.response.SkillResponse;
import com.relatiolab.dto.response.StudentProfileResponse;
import com.relatiolab.dto.response.StudentResponse;
import com.relatiolab.entity.Course;
import com.relatiolab.entity.Enrollment;
import com.relatiolab.entity.Mentor;
import com.relatiolab.entity.Skill;
import com.relatiolab.entity.Student;
import com.relatiolab.entity.StudentProfile;
import java.util.Collections;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public StudentResponse toStudent(Student student) {
        StudentProfileResponse profileResponse = toStudentProfile(student.getProfile());
        return new StudentResponse(student.getId(), student.getName(), student.getEmail(), student.getCreatedAt(),
                profileResponse,
                student.getEnrollments() == null ? Collections.emptyList() : student.getEnrollments().stream().map(this::toEnrollment).toList());
    }

    public StudentProfileResponse toStudentProfile(StudentProfile profile) {
        if (profile == null) {
            return null;
        }
        return new StudentProfileResponse(profile.getId(), profile.getPhone(), profile.getAddress(), profile.getLinkedinUrl());
    }

    public EnrollmentResponse toEnrollment(Enrollment enrollment) {
        return new EnrollmentResponse(enrollment.getId(),
                enrollment.getStudent() != null ? enrollment.getStudent().getId() : null,
                enrollment.getStudent() != null ? enrollment.getStudent().getName() : null,
                enrollment.getCourse() != null ? enrollment.getCourse().getId() : null,
                enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null,
                enrollment.getProgressPercent(), enrollment.getStatus(), enrollment.getEnrolledAt());
    }

    public CourseResponse toCourse(Course course) {
        return new CourseResponse(course.getId(), course.getTitle(), course.getFee(), course.getActive(),
                course.getMentors().stream().map(m -> new MentorSimpleResponse(m.getId(), m.getName())).collect(java.util.stream.Collectors.toSet()));
    }

    public MentorResponse toMentor(Mentor mentor) {
        return new MentorResponse(
                mentor.getId(),
                mentor.getName(),
                mentor.getExpertiseLevel(),
                mentor.getCourses().stream().map(c -> new CourseSimpleResponse(c.getId(), c.getTitle())).collect(java.util.stream.Collectors.toSet()),
                mentor.getSkills().stream().map(this::toSkill).collect(java.util.stream.Collectors.toSet())
        );
    }

    public SkillResponse toSkill(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getCode(), skill.getDisplayName());
    }
}