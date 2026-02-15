package com.relatiolab.service;

import com.relatiolab.dto.request.CreateCourseRequest;
import com.relatiolab.dto.response.CourseResponse;
import com.relatiolab.entity.Course;
import com.relatiolab.entity.Mentor;
import com.relatiolab.exception.ResourceNotFoundException;
import com.relatiolab.repository.CourseRepository;
import com.relatiolab.repository.MentorRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final MentorRepository mentorRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public CourseResponse create(CreateCourseRequest request) {
        Course course = new Course();
        course.setTitle(request.title());
        course.setFee(request.fee());
        if (request.active() != null) {
            course.setActive(request.active());
        }
        return dtoMapper.toCourse(courseRepository.save(course));
    }

    @Transactional
    public List<CourseResponse> list() {
        return courseRepository.findAllWithMentorsGraph().stream().map(dtoMapper::toCourse).toList();
    }

    @Transactional
    public CourseResponse get(Long id) {
        Course course = courseRepository.findWithMentorsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
        return dtoMapper.toCourse(course);
    }

    @Transactional
    public CourseResponse linkMentor(Long courseId, Long mentorId) {
        Course course = courseRepository.findWithMentorsById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + mentorId));
        course.addMentor(mentor);
        return dtoMapper.toCourse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse unlinkMentor(Long courseId, Long mentorId) {
        Course course = courseRepository.findWithMentorsById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + mentorId));
        course.removeMentor(mentor);
        return dtoMapper.toCourse(courseRepository.save(course));
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found: " + id);
        }
        courseRepository.deleteById(id);
    }
}