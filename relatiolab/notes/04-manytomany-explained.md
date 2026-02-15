# ManyToMany Explained

## Concept
Mentor multiple courses handle chestadu; course multiple mentors kaligi untundi. Join table mandatory.

## Visual
```text
mentors --< mentor_courses >-- courses
mentors --< mentor_skills >-- skills
```

## Code Snippet
```java
@ManyToMany
@JoinTable(name = "mentor_courses",
  joinColumns = @JoinColumn(name = "course_id"),
  inverseJoinColumns = @JoinColumn(name = "mentor_id"))
private Set<Mentor> mentors;
```
Annotation: `Course` owning side.

## Common Mistakes
1. Cascade REMOVE use ???? shared rows delete ?????.
2. equals/hashCode ??? Set duplicates issue.
3. unlink logic lo both sides update cheyakapovadam.

## Interview Talking Points
- "ManyToMany ki join table essential."
- "Link remove means join row delete, entity delete ????."

## Related Files
- `backend/src/main/java/com/relatiolab/entity/Course.java`
- `backend/src/main/java/com/relatiolab/entity/Mentor.java`
- `backend/src/main/java/com/relatiolab/service/CourseService.java`