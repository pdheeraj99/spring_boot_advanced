# Course Platform Demo - Complete Notes

## 1) Why this project is easy to remember
- One story: students enroll in courses taught by instructors.
- Each JPA relationship appears naturally in that story.
- You can explain every annotation in business language, not only technical language.

## 2) Thinking process before writing entities
- Step 1: Identify core nouns (`Student`, `Course`, `Instructor`).
- Step 2: Identify metadata that belongs to one owner (`StudentProfile`, `InstructorProfile`, `CertificateTemplate`).
- Step 3: Identify repeated child collections (`Lesson`, `Enrollment`).
- Step 4: Identify flexible labels (`Tag`) and social links (`study buddies`).
- Step 5: Decide join-entity requirement: when extra columns are needed, do not use direct `@ManyToMany` (`Enrollment` has progress, pricePaid, enrolledAt).

## 3) Annotation choice and why
- `@OneToOne(mappedBy=...)`: used where child table stores FK (`StudentProfile.student_id`, `InstructorProfile.instructor_id`).
- `@ManyToOne`: FK belongs on many side (`Lesson.course_id`, `Enrollment.student_id`, `Enrollment.course_id`).
- `@OneToMany(mappedBy=..., cascade=ALL, orphanRemoval=true)`: parent manages child lifecycle (course->lessons, student->enrollments).
- `@ManyToMany` simple: course-tags because join table has only two FKs.
- `@ManyToMany` self-reference: students as study buddies via `student_buddies`.
- `@OneToOne` course-certificate: one course maps to one certificate template design.

## 4) Owning side vs inverse side decisions
- Owning side writes FK/join rows.
- Inverse side (`mappedBy`) mirrors relationship and avoids duplicate join metadata.
- Helper methods (`addLesson`, `addTag`, `addBuddy`) keep both sides synchronized in memory.

## 5) LAZY vs EAGER strategy
- Default to `LAZY` for most associations to avoid unnecessary loading.
- Create optimized repository methods for read-heavy endpoints (`JOIN FETCH` with split-query pattern).
- Avoid loading multiple bag collections in one query; fetch one in main query and another in follow-up query.

## 6) V1 vs V2 learning pattern
- V1 endpoints intentionally use simpler repository calls so lazy loading behavior is visible.
- V2 endpoints use fetch-optimized queries and should execute fewer SQL statements.
- `QueryAnalysisService` uses Hibernate statistics to compare query counts.

## 7) Common pitfalls and fixes
- Pitfall: duplicate enrollment rows.
  - Fix: DB unique constraint on `(student_id, course_id)` + service-level conflict check.
- Pitfall: stale relationship sides.
  - Fix: always use sync helper methods when linking entities.
- Pitfall: infinite JSON recursion.
  - Fix: always return DTOs, never entities from controllers.
- Pitfall: non-deterministic demo behavior.
  - Fix: deterministic seed data with fixed random seed.

## 8) API walkthrough
- V1 paths: create/read + relationship operations.
- V2 paths: optimized read variants.
- Test endpoints: smoke suite and query comparison.
- Metrics endpoint: latest query comparison snapshot.

## 9) How to approach interviews using this project
- Start with domain story.
- Map each story to annotation.
- Explain ownership and cascade choice.
- Show one optimization example (V1 vs V2 query count).
- Close with one data-integrity rule (unique enrollment).

## 10) Quick run commands
```powershell
cd d:\spring_boot_advanced_demos\course-platform-demo
mvn clean test
mvn spring-boot:run
Invoke-RestMethod -Method Post http://localhost:8080/api/test/run-smoke-suite
```