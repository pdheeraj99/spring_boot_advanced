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
 * @ManyToMany Relationship — INVERSE (non-owning) SIDE
 *             ============================================
 *
 *             Real-World Analogy: Course has many Students
 *
 *             IMPORTANT CONCEPTS:
 *
 *             1. mappedBy = "courses" →
 *             - "Nenu inverse side, Student entity lo 'courses' field owner"
 *             - Course side nunchi changes chesthe DB lo persist AVVADU!
 *             - Student (owning side) nunchi maatrame changes persist avutayi
 *
 *             2. idi the INVERSE SIDE (non-owning):
 *             - @JoinTable ikkada ledu — Student lo undi
 *             - mappedBy undi → inverse side confirm
 *
 *             CRITICAL RULE:
 *             Owner side = @JoinTable unna side
 *             Inverse side = mappedBy unna side
 *
 *             INTERVIEW QUESTION: "ManyToMany lo changes ekkada nunchi persist
 *             avutayi?"
 *             ANSWER: Only from the OWNING side! (Student side from this
 *             example)
 *             course.getStudents().add(student) ALONE chesthe persist AVVADU!
 *             student.getCourses().add(course) kavali — because student is the
 *             owner.
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    /**
     * mappedBy = "courses" → Student.courses field ki maps
     * Changes from this side ALONE won't persist to DB!
     */
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();

    public Course(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
