package com.relatiolab.bootstrap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "relatiolab.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MassiveDataSeeder implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${relatiolab.seed.batch-size:5000}")
    private int batchSize;

    @Value("${relatiolab.seed.students:100000}")
    private int studentCount;

    @Value("${relatiolab.seed.courses:20000}")
    private int courseCount;

    @Value("${relatiolab.seed.mentors:5000}")
    private int mentorCount;

    @Value("${relatiolab.seed.skills:1000}")
    private int skillCount;

    @Value("${relatiolab.seed.enrollments:700000}")
    private int enrollmentCount;

    @Value("${relatiolab.seed.mentor-courses:50000}")
    private int mentorCourseCount;

    @Value("${relatiolab.seed.mentor-skills:24000}")
    private int mentorSkillCount;

    @Override
    public void run(ApplicationArguments args) {
        StopWatch stopWatch = new StopWatch("massive-seed");
        stopWatch.start();

        long studentStartId = nextId("students");
        long courseStartId = nextId("courses");
        long mentorStartId = nextId("mentors");
        long skillStartId = nextId("skills");

        log.info("Massive seed started. This may take a few minutes...");
        log.info("Seeding targets students={}, courses={}, mentors={}, skills={}, enrollments={}",
                studentCount, courseCount, mentorCount, skillCount, enrollmentCount);

        insertStudents(studentStartId);
        insertCourses(courseStartId);
        insertMentors(mentorStartId);
        insertSkills(skillStartId);
        insertProfiles(studentStartId);
        insertEnrollments(studentStartId, courseStartId);
        insertMentorCourses(courseStartId, mentorStartId);
        insertMentorSkills(mentorStartId, skillStartId);

        stopWatch.stop();
        log.info("Massive seed completed in {} ms", stopWatch.getTotalTimeMillis());
    }

    private long nextId(String table) {
        Long maxId = jdbcTemplate.queryForObject("select coalesce(max(id),0) from " + table, Long.class);
        return maxId == null ? 1L : maxId + 1;
    }

    private void insertStudents(long startId) {
        LocalDateTime now = LocalDateTime.now();
        runBatch(studentCount,
                "insert into students (id, created_at, email, name) values (?, ?, ?, ?)",
                i -> new Object[]{startId + i, now, "seed.student." + (startId + i) + "@relatiolab.dev", "Seed Student " + (startId + i)});
    }

    private void insertCourses(long startId) {
        runBatch(courseCount,
                "insert into courses (id, active, fee, title) values (?, ?, ?, ?)",
                i -> new Object[]{startId + i, true, 1500 + (i % 400), "Seed Course " + (startId + i)});
    }

    private void insertMentors(long startId) {
        runBatch(mentorCount,
                "insert into mentors (id, expertise_level, name) values (?, ?, ?)",
                i -> new Object[]{startId + i, (i % 2 == 0) ? "SENIOR" : "LEAD", "Seed Mentor " + (startId + i)});
    }

    private void insertSkills(long startId) {
        runBatch(skillCount,
                "insert into skills (id, code, display_name) values (?, ?, ?)",
                i -> new Object[]{startId + i, "SEED_SKILL_" + (startId + i), "Seed Skill " + (startId + i)});
    }

    private void insertProfiles(long studentStartId) {
        runBatch(studentCount,
                "insert into student_profiles (student_id, address, linkedin_url, phone) values (?, ?, ?, ?)",
                i -> {
                    long studentId = studentStartId + i;
                    return new Object[]{studentId, "Seed City " + (i % 250), "https://linkedin.com/in/seed-" + studentId, String.format("9%09d", i % 1_000_000_000)};
                });
    }

    private void insertEnrollments(long studentStartId, long courseStartId) {
        LocalDateTime now = LocalDateTime.now();
        runBatch(enrollmentCount,
                "insert into enrollments (course_id, enrolled_at, progress_percent, status, student_id) values (?, ?, ?, ?, ?)",
                i -> {
                    long studentId = studentStartId + (i % studentCount);
                    long courseId = courseStartId + ((i / studentCount) % courseCount);
                    int progress = i % 101;
                    String status = progress == 100 ? "COMPLETED" : "ACTIVE";
                    return new Object[]{courseId, now, progress, status, studentId};
                });
    }

    private void insertMentorCourses(long courseStartId, long mentorStartId) {
        runBatch(mentorCourseCount,
                "insert into mentor_courses (course_id, mentor_id) values (?, ?)",
                i -> {
                    long courseId = courseStartId + (i % courseCount);
                    long mentorId = mentorStartId + ((i / courseCount) % mentorCount);
                    return new Object[]{courseId, mentorId};
                });
    }

    private void insertMentorSkills(long mentorStartId, long skillStartId) {
        runBatch(mentorSkillCount,
                "insert into mentor_skills (mentor_id, skill_id) values (?, ?)",
                i -> {
                    long mentorId = mentorStartId + (i % mentorCount);
                    long skillId = skillStartId + ((i / mentorCount) % skillCount);
                    return new Object[]{mentorId, skillId};
                });
    }

    private void runBatch(int total, String sql, RowSupplier rowSupplier) {
        for (int from = 0; from < total; from += batchSize) {
            int to = Math.min(from + batchSize, total);
            List<Object[]> args = new ArrayList<>(to - from);
            for (int i = from; i < to; i++) {
                args.add(rowSupplier.row(i));
            }
            jdbcTemplate.batchUpdate(sql, args);
            if ((to % 100_000) == 0 || to == total) {
                log.info("Seed progress for [{}]: {}/{}", sql, to, total);
            }
        }
    }

    @FunctionalInterface
    private interface RowSupplier {
        Object[] row(int i);
    }
}
