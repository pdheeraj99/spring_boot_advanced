package com.example.courseplatformdemo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    // mappedBy indicates StudentProfile owns the foreign key.
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private StudentProfile profile;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "student_buddies",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "buddy_id"))
    private Set<Student> studyBuddies = new HashSet<>();

    @ManyToMany(mappedBy = "studyBuddies")
    private Set<Student> buddyOf = new HashSet<>();

    public void setProfile(StudentProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setStudent(this);
        }
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setStudent(this);
    }

    public void addBuddy(Student buddy) {
        if (buddy == null || buddy.equals(this)) {
            return;
        }
        this.studyBuddies.add(buddy);
        buddy.getStudyBuddies().add(this);
    }
}