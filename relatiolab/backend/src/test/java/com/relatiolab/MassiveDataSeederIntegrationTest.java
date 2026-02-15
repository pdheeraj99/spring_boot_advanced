package com.relatiolab;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = {
        "relatiolab.seed.enabled=true",
        "relatiolab.seed.batch-size=10",
        "relatiolab.seed.students=20",
        "relatiolab.seed.courses=8",
        "relatiolab.seed.mentors=6",
        "relatiolab.seed.skills=4",
        "relatiolab.seed.enrollments=30",
        "relatiolab.seed.mentor-courses=10",
        "relatiolab.seed.mentor-skills=8"
})
class MassiveDataSeederIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void startupSeederShouldInsertConfiguredCounts() {
        assertThat(count("students")).isGreaterThanOrEqualTo(20);
        assertThat(count("student_profiles")).isGreaterThanOrEqualTo(20);
        assertThat(count("courses")).isGreaterThanOrEqualTo(8);
        assertThat(count("mentors")).isGreaterThanOrEqualTo(6);
        assertThat(count("skills")).isGreaterThanOrEqualTo(4);
        assertThat(count("enrollments")).isGreaterThanOrEqualTo(30);
        assertThat(count("mentor_courses")).isGreaterThanOrEqualTo(10);
        assertThat(count("mentor_skills")).isGreaterThanOrEqualTo(8);
    }

    private long count(String table) {
        Long value = jdbcTemplate.queryForObject("select count(*) from " + table, Long.class);
        return value == null ? 0 : value;
    }
}