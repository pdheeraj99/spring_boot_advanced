# JPA Relationships Theory

## Concept
`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany` anni table foreign key/join table based relations.

## Visual
```text
OneToOne: A --1:1-- B
OneToMany: A --1:*-- B
ManyToOne: B --*:1-- A
ManyToMany: A --*:*-- B via join table
```

## Code Snippet
```java
@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Enrollment> enrollments;
```
Annotation: `mappedBy` unte inverse side.

## Common Mistakes
1. Owning side identify cheyakapovadam.
2. Both sides sync cheyakapovadam.
3. Cascade remove ni shared entity meeda use cheyadam.

## Interview Talking Points
- "Owning side has FK/join table definition."
- "mappedBy indicates inverse side only."

## Related Files
- `backend/src/main/java/com/relatiolab/entity/Student.java`
- `backend/src/main/java/com/relatiolab/entity/Enrollment.java`
- `backend/src/main/java/com/relatiolab/entity/Mentor.java`