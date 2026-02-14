package com.example.courseplatformdemo.loader;

import com.example.courseplatformdemo.entity.CertificateTemplate;
import com.example.courseplatformdemo.entity.Course;
import com.example.courseplatformdemo.entity.Enrollment;
import com.example.courseplatformdemo.entity.Instructor;
import com.example.courseplatformdemo.entity.InstructorProfile;
import com.example.courseplatformdemo.entity.Lesson;
import com.example.courseplatformdemo.entity.Student;
import com.example.courseplatformdemo.entity.StudentProfile;
import com.example.courseplatformdemo.entity.Tag;
import com.example.courseplatformdemo.repository.CourseRepository;
import com.example.courseplatformdemo.repository.EnrollmentRepository;
import com.example.courseplatformdemo.repository.InstructorRepository;
import com.example.courseplatformdemo.repository.StudentRepository;
import com.example.courseplatformdemo.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final EnrollmentRepository enrollmentRepository;

    @PostConstruct
    public void load() {
        if (studentRepository.count() > 0 || courseRepository.count() > 0) {
            return;
        }

        Random random = new Random(42);

        List<Tag> tags = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Tag tag = new Tag();
            tag.setName("Tag-" + i);
            tags.add(tag);
        }
        tags = tagRepository.saveAll(tags);

        List<Instructor> instructors = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Instructor instructor = new Instructor();
            instructor.setName("Instructor " + i);
            instructor.setEmail("instructor" + i + "@example.com");

            InstructorProfile profile = new InstructorProfile();
            profile.setHeadline("Expert Instructor " + i);
            profile.setExpertise("Track " + ((i % 3) + 1));
            profile.setYearsExperience(5 + i);
            instructor.setProfile(profile);

            instructors.add(instructor);
        }
        instructors = instructorRepository.saveAll(instructors);

        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Student student = new Student();
            student.setName("Student " + i);
            student.setEmail("student" + i + "@example.com");

            StudentProfile profile = new StudentProfile();
            profile.setBio("Learner bio " + i);
            profile.setPhoneNumber("90000000" + String.format("%02d", i));
            profile.setDateOfBirth(LocalDate.of(1995, 1, 1).plusDays(i * 40L));
            student.setProfile(profile);

            students.add(student);
        }
        students = studentRepository.saveAll(students);

        for (int i = 0; i < students.size() - 1; i++) {
            students.get(i).addBuddy(students.get(i + 1));
        }
        studentRepository.saveAll(students);

        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Course course = new Course();
            course.setTitle("Course " + i);
            course.setDescription("Advanced course description " + i);
            course.setPrice(BigDecimal.valueOf(49 + i * 10L));
            course.setPublished(i % 2 == 0);
            course.setInstructor(instructors.get(random.nextInt(instructors.size())));

            CertificateTemplate template = new CertificateTemplate();
            template.setTemplateName("Template " + i);
            template.setHeaderText("Certificate Header " + i);
            template.setFooterText("Certificate Footer " + i);
            course.setCertificateTemplate(template);

            for (int j = 1; j <= 5; j++) {
                Lesson lesson = new Lesson();
                lesson.setTitle("Course " + i + " Lesson " + j);
                lesson.setVideoUrl("https://videos.example.com/" + i + "/" + j);
                lesson.setDurationMinutes(8 + random.nextInt(15));
                lesson.setSortOrder(j);
                course.addLesson(lesson);
            }

            for (int t = 0; t < 3; t++) {
                course.addTag(tags.get(random.nextInt(tags.size())));
            }

            courses.add(course);
        }
        courses = courseRepository.saveAll(courses);

        List<Enrollment> enrollments = new ArrayList<>();
        while (enrollments.size() < 25) {
            Student student = students.get(random.nextInt(students.size()));
            Course course = courses.get(random.nextInt(courses.size()));

            boolean exists = enrollments.stream().anyMatch(e ->
                    e.getStudent().getId().equals(student.getId()) && e.getCourse().getId().equals(course.getId()));
            if (exists) {
                continue;
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrolledAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            int progress = random.nextInt(101);
            enrollment.setProgressPercent(progress);
            enrollment.setCompleted(progress == 100);
            enrollment.setPricePaid(course.getPrice());
            enrollments.add(enrollment);
        }
        enrollmentRepository.saveAll(enrollments);

        log.info("Seeded students={}, instructors={}, courses={}, lessons={}, tags={}, enrollments={}",
                students.size(),
                instructors.size(),
                courses.size(),
                60,
                tags.size(),
                enrollments.size());
    }
}
