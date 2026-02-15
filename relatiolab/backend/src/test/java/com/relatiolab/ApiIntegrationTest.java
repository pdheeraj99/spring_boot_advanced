package com.relatiolab;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void duplicateEnrollmentShouldReturn409() throws Exception {
        String student = "{\"name\":\"Ravi\",\"email\":\"ravi@test.com\"}";
        String course = "{\"title\":\"JPA\",\"fee\":1000,\"active\":true}";
        mockMvc.perform(post("/api/v1/students").contentType(MediaType.APPLICATION_JSON).content(student))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON).content(course))
                .andExpect(status().isCreated());

        String enrollment = "{\"studentId\":1,\"courseId\":1,\"progressPercent\":10}";
        mockMvc.perform(post("/api/v1/enrollments").contentType(MediaType.APPLICATION_JSON).content(enrollment))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/enrollments").contentType(MediaType.APPLICATION_JSON).content(enrollment))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("CONFLICT")));
    }

    @Test
    void debugSqlEndpointShouldReturnRequestIdAndTraces() throws Exception {
        mockMvc.perform(get("/api/v1/debug/sql/recent"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"));
    }

    @Test
    void nPlusOneBadModeShouldReturnCounts() throws Exception {
        mockMvc.perform(get("/api/v1/debug/nplus1/students-with-enrollments").param("mode", "bad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queryCount", greaterThan(-1)))
                .andExpect(jsonPath("$.scenario", is("students-enrollments")));
    }
}