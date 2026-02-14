package com.example.courseplatformdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "instructor_profiles")
@Getter
@Setter
public class InstructorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String headline;

    private String expertise;

    private Integer yearsExperience;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", unique = true)
    private Instructor instructor;
}