# Fetch Strategies

## Concept
N+1 avoid cheyadaniki 3 key tools: JOIN FETCH, `@EntityGraph`, Batch Fetching.

## Visual
```mermaid
flowchart LR
BAD[Lazy loop] --> N1[N+1 queries]
JOIN[JOIN FETCH] --> ONE[Single/fewer queries]
GRAPH[@EntityGraph] --> ONE
BATCH[Batch Fetch] --> CHUNK[Chunked selects]
```

## Code Snippet
```java
@Query("select distinct s from Student s left join fetch s.enrollments e left join fetch e.course")
List<Student> findAllWithEnrollmentsJoinFetch();
```
Annotation: interview comments already repository lo add chesam.

## Common Mistakes
1. Large graph ni JOIN FETCH chesi cartesian explosion.
2. EntityGraph paths typo.
3. Batch size set chesi lazy access trigger cheyakapovadam.

## Interview Talking Points
- "JOIN FETCH for focused read use-case."
- "EntityGraph for reusable fetch plan."
- "Batch for list views with lazy associations."

## Related Files
- `backend/src/main/java/com/relatiolab/repository/StudentRepository.java`
- `backend/src/main/java/com/relatiolab/repository/CourseRepository.java`
- `backend/src/main/resources/application.properties`