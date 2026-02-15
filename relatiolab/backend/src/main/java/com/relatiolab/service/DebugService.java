package com.relatiolab.service;

import com.relatiolab.debug.RequestTraceContext;
import com.relatiolab.debug.SqlTraceEntry;
import com.relatiolab.debug.SqlTraceStore;
import com.relatiolab.dto.response.FetchComparisonResponse;
import com.relatiolab.dto.response.SqlSummaryResponse;
import com.relatiolab.dto.response.SqlTraceResponse;
import com.relatiolab.entity.Course;
import com.relatiolab.entity.Enrollment;
import com.relatiolab.entity.Student;
import com.relatiolab.repository.CourseRepository;
import com.relatiolab.repository.EnrollmentRepository;
import com.relatiolab.repository.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebugService {

    private final SqlTraceStore sqlTraceStore;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DtoMapper dtoMapper;

    public List<SqlTraceResponse> recentSql(int limit) {
        return sqlTraceStore.recent(limit).stream().map(this::toSqlTraceResponse).toList();
    }

    public List<SqlTraceResponse> sqlByRequestId(String requestId) {
        return sqlTraceStore.byRequestId(requestId).stream().map(this::toSqlTraceResponse).toList();
    }

    public void clear() {
        sqlTraceStore.clear();
    }

    public SqlSummaryResponse summary() {
        List<SqlTraceEntry> snapshot = sqlTraceStore.snapshot();
        return new SqlSummaryResponse(
                snapshot.size(),
                snapshot.stream().filter(s -> "SELECT".equals(s.getOperation())).count(),
                snapshot.stream().filter(s -> "INSERT".equals(s.getOperation())).count(),
                snapshot.stream().filter(s -> "UPDATE".equals(s.getOperation())).count(),
                snapshot.stream().filter(s -> "DELETE".equals(s.getOperation())).count()
        );
    }

    @Transactional
    public FetchComparisonResponse fetchComparison(String mode) {
        long start = System.currentTimeMillis();
        Object data = switch (mode) {
            case "join-fetch" -> studentRepository.findAllWithEnrollmentsJoinFetch().stream().map(dtoMapper::toStudent).toList();
            case "entity-graph" -> studentRepository.findAllWithGraph().stream().map(dtoMapper::toStudent).toList();
            case "batch" -> studentRepository.findAll().stream().map(s -> {
                s.getEnrollments().size();
                return dtoMapper.toStudent(s);
            }).toList();
            default -> studentRepository.findAll().stream().map(dtoMapper::toStudent).toList();
        };
        return collectComparison("student-dashboard", mode, start, data);
    }

    @Transactional
    public FetchComparisonResponse nPlusOneStudents(String mode) {
        long start = System.currentTimeMillis();
        List<Student> students;
        if ("join-fetch".equals(mode)) {
            students = studentRepository.findAllWithEnrollmentsJoinFetch();
        } else if ("entity-graph".equals(mode)) {
            students = studentRepository.findAllWithGraph();
        } else {
            // Problem: This causes N+1 - one query for students, then N queries for enrollments.
            // Fix: Use JOIN FETCH or @EntityGraph or batch fetching.
            // When not to use: Interview demos where you need to intentionally show bad path.
            students = studentRepository.findAll();
        }

        List<?> data = students.stream().map(s -> {
            if ("bad".equals(mode) || "batch".equals(mode)) {
                s.getEnrollments().size();
            }
            return dtoMapper.toStudent(s);
        }).toList();

        return collectComparison("students-enrollments", mode, start, data);
    }

    @Transactional
    public FetchComparisonResponse nPlusOneCourses(String mode) {
        long start = System.currentTimeMillis();
        List<Course> courses = "bad".equals(mode) ? courseRepository.findAll() : courseRepository.findAllWithMentorsGraph();
        List<?> data = courses.stream().map(c -> {
            c.getMentors().size();
            return dtoMapper.toCourse(c);
        }).toList();
        return collectComparison("courses-mentors", mode, start, data);
    }

    @Transactional
    public FetchComparisonResponse nPlusOneEnrollmentReport(String mode) {
        long start = System.currentTimeMillis();
        List<Enrollment> enrollments = "join-fetch".equals(mode)
                ? enrollmentRepository.findAllReportJoinFetch()
                : enrollmentRepository.findAll();
        List<?> data = enrollments.stream().map(e -> {
            if ("bad".equals(mode)) {
                e.getStudent().getName();
                e.getCourse().getTitle();
            }
            return dtoMapper.toEnrollment(e);
        }).toList();
        return collectComparison("enrollment-report", mode, start, data);
    }

    private FetchComparisonResponse collectComparison(String scenario, String mode, long startMs, Object data) {
        String requestId = RequestTraceContext.requestId();
        List<SqlTraceEntry> traces = requestId == null ? List.of() : sqlTraceStore.byRequestId(requestId);
        long selectCount = traces.stream().filter(t -> "SELECT".equals(t.getOperation())).count();
        return new FetchComparisonResponse(scenario, mode, traces.size(), selectCount,
                System.currentTimeMillis() - startMs, data);
    }

    private SqlTraceResponse toSqlTraceResponse(SqlTraceEntry entry) {
        return new SqlTraceResponse(entry.getTimestamp(), entry.getRequestId(), entry.getMethod(), entry.getPath(),
                entry.getOperation(), entry.getSql());
    }
}