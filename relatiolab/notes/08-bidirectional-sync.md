# Bidirectional Sync

## Concept
Two-way relation lo ???? side ??????? update ?????? memory graph inconsistent ????????.

## Visual
```mermaid
flowchart LR
A[student.addEnrollment] --> B[enrollment.setStudent(student)]
C[course.addMentor] --> D[mentor.getCourses().add(course)]
```

## Code Snippet
```java
public void addMentor(Mentor mentor) {
    mentors.add(mentor);
    mentor.getCourses().add(this);
}
```
Annotation: both sides sync.

## Common Mistakes
1. Parent list ?? add ???? child reference set ???????????.
2. remove ?????? opposite side clean ???????????.
3. Helper methods bypass ?????.

## Interview Talking Points
- "Helper methods prevent inconsistent object graph bugs."
- "Persistence context flush ?????? unexpected SQL reduce ????????."

## Related Files
- `backend/src/main/java/com/relatiolab/entity/Student.java`
- `backend/src/main/java/com/relatiolab/entity/Course.java`
- `backend/src/main/java/com/relatiolab/entity/Mentor.java`