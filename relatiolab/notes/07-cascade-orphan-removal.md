# Cascade and Orphan Removal

## Concept
Parent-child lifecycle strong ????? cascade/orphanRemoval use chestam.

## Visual
```text
Student --(cascade ALL + orphanRemoval)--> StudentProfile
Student --(cascade ALL + orphanRemoval)--> Enrollment
```

## Code Snippet
```java
@OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
private StudentProfile profile;
```
Annotation: profile remove -> DB row delete.

## Common Mistakes
1. Shared entity (Skill) ??? cascade remove.
2. orphanRemoval true ?????? child list manipulation wrong ?? ?????.
3. bidirectional sync ??? orphan cleanup miss ????.

## Interview Talking Points
- "Cascade propagates operations; orphanRemoval handles detached child deletion."
- "Use carefully only aggregate boundaries ??."

## Related Files
- `backend/src/main/java/com/relatiolab/entity/Student.java`
- `backend/src/main/java/com/relatiolab/entity/Course.java`
- `backend/src/test/java/com/relatiolab/RelationshipMappingDataJpaTest.java`