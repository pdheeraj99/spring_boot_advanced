package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.CreateCourseRequest;
import com.example.courseplatformdemo.dto.CreateLessonRequest;
import com.example.courseplatformdemo.dto.CourseResponse;
import com.example.courseplatformdemo.entity.CertificateTemplate;
import com.example.courseplatformdemo.entity.Course;
import com.example.courseplatformdemo.entity.Instructor;
import com.example.courseplatformdemo.entity.Lesson;
import com.example.courseplatformdemo.entity.Tag;
import com.example.courseplatformdemo.exception.ResourceNotFoundException;
import com.example.courseplatformdemo.repository.CourseRepository;
import com.example.courseplatformdemo.repository.TagRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final InstructorService instructorService;
    private final DtoMapper dtoMapper;

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        Instructor instructor = instructorService.getInstructorEntity(request.instructorId());

        Course course = new Course();
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setPrice(request.price());
        course.setPublished(request.published());
        course.setInstructor(instructor);

        if (request.tagNames() != null) {
            for (String tagName : request.tagNames()) {
                if (tagName == null || tagName.isBlank()) {
                    continue;
                }
                Tag tag = tagRepository.findByNameIgnoreCase(tagName.trim())
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName.trim());
                            return tagRepository.save(newTag);
                        });
                course.addTag(tag);
            }
        }

        CertificateTemplate template = new CertificateTemplate();
        template.setTemplateName(request.certificateTemplateName());
        template.setHeaderText(request.certificateHeaderText());
        template.setFooterText(request.certificateFooterText());
        course.setCertificateTemplate(template);

        return dtoMapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse addLesson(Long courseId, CreateLessonRequest request) {
        Course course = getCourseEntity(courseId);
        Lesson lesson = new Lesson();
        lesson.setTitle(request.title());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setDurationMinutes(request.durationMinutes());
        lesson.setSortOrder(request.sortOrder());
        course.addLesson(lesson);
        return dtoMapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCoursesV1() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(dtoMapper::toCourseResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCoursesV2() {
        List<Course> courses = courseRepository.findAllDetailedWithJoinFetch();
        List<Long> ids = courses.stream().map(Course::getId).toList();
        if (!ids.isEmpty()) {
            courseRepository.fetchLessonsForCourses(ids);
        }
        return courses.stream().map(dtoMapper::toCourseResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTag(String tagName) {
        return courseRepository.findByTagName(tagName).stream().map(dtoMapper::toCourseResponse).toList();
    }

    @Transactional(readOnly = true)
    public Course getCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
    }

    @Transactional
    public void addTagToCourse(Course course, String tagName) {
        if (tagName == null || tagName.isBlank()) {
            return;
        }
        Tag tag = tagRepository.findByNameIgnoreCase(tagName.trim())
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName.trim());
                    return tagRepository.save(newTag);
                });
        course.addTag(tag);
    }

    public Set<String> normalizeTags(Set<String> rawTags) {
        if (rawTags == null) {
            return Set.of();
        }
        Set<String> result = new HashSet<>();
        for (String raw : rawTags) {
            if (raw != null && !raw.isBlank()) {
                result.add(raw.trim());
            }
        }
        return result;
    }
}