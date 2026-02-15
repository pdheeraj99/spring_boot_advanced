package com.example.jpaacademy.onetoone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ============================================
 * 
 * @OneToOne Relationship — OWNING SIDE (FK holder)
 *           ============================================
 *
 *           Real-World Analogy: Wife belongs to one Husband
 *
 *           IMPORTANT CONCEPTS:
 *           1. @JoinColumn(name = "husband_id") → wives table lo "husband_id"
 *           FK column create avutundi
 *           - This makes Wife the OWNING SIDE
 *           - Owner ante DB lo FK column unna entity
 *           2. unique = true → @JoinColumn lo unique ante ONE wife per husband
 *           enforce avutundi
 *           3. fetch = LAZY → Performance kosam, Husband data need ayyinappudu
 *           maatrame load avutundi
 *
 *           TABLE STRUCTURE:
 *           ┌──────────────────────────┐
 *           │ wives table │
 *           ├──────────────────────────┤
 *           │ id (PK) │
 *           │ name │
 *           │ age │
 *           │ husband_id (FK, UNIQUE) │ ← This FK creates the OneToOne link!
 *           └──────────────────────────┘
 */
@Entity
@Table(name = "wives")
@Getter
@Setter
@NoArgsConstructor
public class Wife {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    /**
     * @JoinColumn → DB lo FK column create chestundi
     *             name = "husband_id" → column name specify
     *             unique = true → oka husband_id ki oka wife maatrame (OneToOne
     *             enforce)
     *             nullable = false → wife ki husband mandatory
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "husband_id", nullable = false, unique = true)
    private Husband husband;

    public Wife(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
