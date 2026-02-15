package com.example.jpaacademy.manytomany.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * ============================================
 * 
 * @ManyToMany Relationship — OWNING SIDE
 *             ============================================
 *
 *             Real-World Analogy: Student ↔ Course
 *             - Oka Student ki MULTIPLE Courses untayi
 *             - Oka Course lo MULTIPLE Students untaru
 *
 *             IMPORTANT CONCEPTS:
 *
 *             1. @ManyToMany → Both sides "many"
 *             - DB lo JOIN TABLE create avutundi — student_courses
 *             - Neither students nor courses table lo FK column radu!
 *             - Separate third table (join table) lo both FKs untayi
 *
 *             2. @JoinTable → Join table configuration
 *             - name = "student_courses" → join table name
 *             - joinColumns → current entity (Student) ki FK
 *             - inverseJoinColumns → opposite entity (Course) ki FK
 *
 *             3. Student is OWNING SIDE because:
 *             - @JoinTable ikkada defined
 *             - Course lo mappedBy = "courses" untundi
 *             - Owning side nunchi changes maatrame DB lo persist avutayi!
 *
 *             JOIN TABLE STRUCTURE:
 *             ┌──────────────────────────────┐
 *             │ student_courses (JOIN TABLE) │
 *             ├──────────────────────────────┤
 *             │ student_id (FK → students) │
 *             │ course_id (FK → courses) │
 *             └──────────────────────────────┘
 *
 *             INTERVIEW QUESTION: "ManyToMany lo FK ekkada untundi?"
 *             ANSWER: Neither side lo! Separate JOIN TABLE lo untundi!
 *
 *             4. Set<Course> use cheddaam (List kaadu):
 *             - Set duplicates allow cheyyadu — same enrollment repeat avvadu
 *             - Hibernate ManyToMany + List use chesthe extra DELETE queries
 *             fire avutayi (performance issue!)
 *             - ALWAYS use Set for ManyToMany collections
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String rollNumber;

    /**
     * @JoinTable creates the join table
     *            joinColumns → Student FK in join table
     *            inverseJoinColumns → Course FK in join table
     *
     *            fetch = LAZY → courses need ayyinappudu maatrame load avutayi
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_courses", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    public Student(String name, String rollNumber) {
        this.name = name;
        this.rollNumber = rollNumber;
    }

    /**
     * Helper: BOTH sides synchronize!
     * student.courses lo add + course.students lo add
     */
    public void enrollInCourse(Course course) {
        this.courses.add(course);
        course.getStudents().add(this);
    }

    public void dropCourse(Course course) {
        this.courses.remove(course);
        course.getStudents().remove(this);
    }
}
