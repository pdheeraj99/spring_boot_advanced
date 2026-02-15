# Interview Questions

## Concept
Common Hibernate interview questions ?? crisp, project-backed answers.

## Visual
```text
Question -> Scenario endpoint -> SQL proof -> Fix tradeoff
```

## Code Snippet
```java
@GetMapping("/api/v1/debug/nplus1/students-with-enrollments")
public FetchComparisonResponse studentsNPlusOne(...) { ... }
```
Annotation: theory ??????? measurable demo.

## Common Mistakes
1. "N+1 ???? lazy" ??? generic answer ??????? ???????.
2. owning side / mappedBy mix-up.
3. cascade vs orphan removal difference ?????????????.

## Interview Talking Points
- Difference between JOIN FETCH and EntityGraph
- When to choose Batch fetching
- Why ManyToMany remove cascade risky
- Why DTO mapping required for REST
- How to test relationship behavior

## Related Files
- `backend/src/main/java/com/relatiolab/repository/StudentRepository.java`
- `backend/src/main/java/com/relatiolab/service/DebugService.java`
- `notes/06-nplus1-problem.md`