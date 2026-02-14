# Course Platform Demo - Quick Sheet

## Relationship Cheat Table
- `Student <-> StudentProfile`: `@OneToOne` (profile owns FK)
- `Instructor <-> InstructorProfile`: `@OneToOne`
- `Course -> Lesson`: `@OneToMany` / `Lesson -> Course`: `@ManyToOne`
- `Student -> Enrollment`: `@OneToMany` / `Enrollment -> Student`: `@ManyToOne`
- `Course -> Enrollment`: `@OneToMany` / `Enrollment -> Course`: `@ManyToOne`
- `Course <-> Tag`: simple `@ManyToMany`
- `Student <-> Course`: modeled via `Enrollment` join entity
- `Student <-> Student`: self `@ManyToMany` for buddies
- `Course <-> CertificateTemplate`: `@OneToOne`

## 20 Interview One-Liners
1. Use join entity when relation carries business attributes.
2. `mappedBy` means this side is inverse and does not own FK.
3. `orphanRemoval=true` deletes detached children from DB.
4. `cascade=ALL` is safe only when child lifecycle is fully owned.
5. Prefer LAZY on collections to avoid loading storms.
6. DTO projection prevents recursion and overfetching.
7. Service methods should enforce uniqueness before DB exception.
8. Unique constraints protect data integrity under concurrency.
9. Split fetch strategy avoids `MultipleBagFetchException`.
10. `JOIN FETCH` removes N+1 in read-heavy paths.
11. Bidirectional helpers keep both sides synchronized.
12. Self many-to-many requires careful symmetric linking.
13. Domain language should drive entity boundaries.
14. Keep controllers thin; put rules in services.
15. Test behavior, not just annotations.
16. Use deterministic seeds for reproducible demos.
17. Validation annotations guard API inputs early.
18. Exception handler gives stable API error contracts.
19. Compare query counts using Hibernate statistics.
20. Versioned APIs are useful for teaching optimization diffs.

## Endpoint Quick List
- `GET /api/v1/courses`
- `GET /api/v1/students/{id}/dashboard`
- `POST /api/v1/students`
- `POST /api/v1/instructors`
- `POST /api/v1/courses`
- `POST /api/v1/courses/{courseId}/lessons`
- `POST /api/v1/enrollments`
- `PATCH /api/v1/enrollments/{id}/progress`
- `POST /api/v1/students/{studentId}/buddies/{buddyId}`
- `GET /api/v1/tags/{tagName}/courses`
- `GET /api/v2/courses/optimized`
- `GET /api/v2/students/{studentId}/dashboard/optimized`
- `POST /api/test/run-smoke-suite`
- `GET /api/test/compare-courses`
- `GET /api/test/compare-dashboard`
- `GET /api/metrics/latest`

## Debug Checklist
- Check owning side and FK columns first.
- Verify helper method updates both sides.
- Confirm transaction boundary around LAZY access.
- Ensure DTO mapper triggers only intended associations.
- Validate unique constraints exist in generated schema.
- Compare query counts for V1 and V2 before claiming optimization.