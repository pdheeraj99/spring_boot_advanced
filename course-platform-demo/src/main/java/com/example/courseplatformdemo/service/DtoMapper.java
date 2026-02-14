package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.CertificateTemplateResponse;
import com.example.courseplatformdemo.dto.CourseResponse;
import com.example.courseplatformdemo.dto.EnrollmentResponse;
import com.example.courseplatformdemo.dto.InstructorProfileResponse;
import com.example.courseplatformdemo.dto.InstructorResponse;
import com.example.courseplatformdemo.dto.LessonResponse;
import com.example.courseplatformdemo.dto.StudentProfileResponse;
import com.example.courseplatformdemo.dto.StudentResponse;
import com.example.courseplatformdemo.dto.TagResponse;
import com.example.courseplatformdemo.entity.Course;
import com.example.courseplatformdemo.entity.Enrollment;
import com.example.courseplatformdemo.entity.Instructor;
import com.example.courseplatformdemo.entity.InstructorProfile;
import com.example.courseplatformdemo.entity.Lesson;
import com.example.courseplatformdemo.entity.Student;
import com.example.courseplatformdemo.entity.StudentProfile;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public StudentResponse toStudentResponse(Student student) {
        StudentProfile profile = student.getProfile();
        StudentProfileResponse profileResponse = profile == null
                ? null
                : new StudentProfileResponse(profile.getId(), profile.getBio(), profile.getPhoneNumber(), profile.getDateOfBirth());
        List<Long> buddyIds = student.getStudyBuddies().stream().map(Student::getId).toList();
        return new StudentResponse(student.getId(), student.getName(), student.getEmail(), profileResponse, buddyIds);
    }

    public InstructorResponse toInstructorResponse(Instructor instructor) {
        InstructorProfile profile = instructor.getProfile();
        InstructorProfileResponse profileResponse = profile == null
                ? null
                : new InstructorProfileResponse(profile.getId(), profile.getHeadline(), profile.getExpertise(), profile.getYearsExperience());
        return new InstructorResponse(instructor.getId(), instructor.getName(), instructor.getEmail(), profileResponse);
    }

    public CourseResponse toCourseResponse(Course course) {
        List<LessonResponse> lessons = course.getLessons().stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(this::toLessonResponse)
                .toList();
        Set<TagResponse> tags = course.getTags().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toSet());

        CertificateTemplateResponse cert = course.getCertificateTemplate() == null
                ? null
                : new CertificateTemplateResponse(
                        course.getCertificateTemplate().getId(),
                        course.getCertificateTemplate().getTemplateName(),
                        course.getCertificateTemplate().getHeaderText(),
                        course.getCertificateTemplate().getFooterText());

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getPublished(),
                course.getInstructor() == null ? null : course.getInstructor().getId(),
                course.getInstructor() == null ? null : course.getInstructor().getName(),
                lessons,
                tags,
                cert);
    }

    public EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        int lessonCount = enrollment.getCourse().getLessons().size();
        Set<String> tagNames = enrollment.getCourse().getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getName(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                lessonCount,
                tagNames,
                enrollment.getEnrolledAt(),
                enrollment.getProgressPercent(),
                enrollment.getCompleted(),
                enrollment.getPricePaid());
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(lesson.getId(), lesson.getTitle(), lesson.getVideoUrl(), lesson.getDurationMinutes(), lesson.getSortOrder());
    }
}
