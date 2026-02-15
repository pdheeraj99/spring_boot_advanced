# N+1 Problem

## Concept
One parent query + each parent?? separate child query = N+1.

## Visual
```text
SELECT students ...      (1)
SELECT enrollments ...   (N times)
Total => N+1
```

## Code Snippet
```java
// Problem: This causes N+1 - one query for students, then N queries for enrollments.
students = studentRepository.findAll();
students.forEach(s -> s.getEnrollments().size());
```
Annotation: intentional bad path debug endpoint lo undi.

## Common Mistakes
1. Only functional correctness ???? performance ignore ???????.
2. SQL logging ??????? tuning try ?????.
3. fix mode verify ???????? assumptions ???????.

## Interview Talking Points
- "I reproduced N+1 with debug endpoint and query count."
- "Then compared join-fetch/entity-graph/batch numerically."

## Related Files
- `backend/src/main/java/com/relatiolab/service/DebugService.java`
- `backend/src/main/java/com/relatiolab/controller/DebugController.java`
- `frontend/src/pages/NPlusOnePage.tsx`