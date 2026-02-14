package com.example.courseplatformdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.courseplatformdemo.repository.CourseRepository;
import com.example.courseplatformdemo.repository.EnrollmentRepository;
import com.example.courseplatformdemo.repository.StudentRepository;
import java.util.AbstractMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void createAndReadCourseFlowWorks() throws Exception {
        mockMvc.perform(post("/api/v1/instructors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"API Instructor","email":"api.instructor@example.com","headline":"Head","expertise":"Spring","yearsExperience":6}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"API Course","description":"desc","price":50,"published":true,"instructorId":1,
                                "tagNames":["Spring","Boot"],"certificateTemplateName":"T1","certificateHeaderText":"H","certificateFooterText":"F"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v2/courses/optimized"))
                .andExpect(status().isOk());
    }

    @Test
    void enrollmentAndProgressEndpointsWork() throws Exception {
        var pair = studentRepository.findAll().stream()
                .flatMap(student -> courseRepository.findAll().stream()
                        .map(course -> new AbstractMap.SimpleEntry<>(student.getId(), course.getId())))
                .filter(ids -> enrollmentRepository.findByStudentIdAndCourseId(ids.getKey(), ids.getValue()).isEmpty())
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentId":%d,"courseId":%d}
                                """.formatted(pair.getKey(), pair.getValue())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Long enrollmentId = enrollmentRepository.findByStudentIdAndCourseId(pair.getKey(), pair.getValue())
                .orElseThrow().getId();

        mockMvc.perform(patch("/api/v1/enrollments/" + enrollmentId + "/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"progressPercent":70}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercent").value(70));
    }

    @Test
    void validationAndNotFoundErrorsWork() throws Exception {
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"bad"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/students/999999/dashboard"))
                .andExpect(status().isNotFound());
    }
}
