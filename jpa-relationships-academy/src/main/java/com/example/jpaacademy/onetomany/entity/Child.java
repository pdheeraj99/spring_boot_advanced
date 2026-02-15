package com.example.jpaacademy.onetomany.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ============================================
 * 
 * @ManyToOne Relationship — OWNING SIDE (FK holder)
 *            ============================================
 *
 *            Real-World Analogy: Child → Mother
 *            - Oka Child ki exactly ONE Mother
 *            - "Many" Children "One" Mother ki belong avutaru
 *
 *            IMPORTANT CONCEPTS:
 *
 *            1. @ManyToOne → Child entity lo Mother ki reference undi
 *            - idi the OWNING SIDE of the relationship
 *            - DB lo children table lo "mother_id" FK column untundi
 *
 *            2. @JoinColumn(name = "mother_id") →
 *            - children table lo FK column name specify
 *            - idi lekunda default "mother_id" ne use chestundi Hibernate
 *
 *            3. fetch = LAZY →
 *            - Default for @ManyToOne is EAGER! (dangerous for performance)
 *            - Manamu explicitly LAZY set cheddaam — best practice
 *            - INTERVIEW QUESTION: "@ManyToOne default fetch type?" → EAGER!
 *
 *            TABLE STRUCTURE:
 *            ┌──────────────────────────┐
 *            │ children table │
 *            ├──────────────────────────┤
 *            │ id (PK) │
 *            │ name │
 *            │ age │
 *            │ mother_id (FK) │ ← This FK creates the ManyToOne link!
 *            └──────────────────────────┘
 *
 *            NOTE: OneToMany & ManyToOne are the SAME relationship from
 *            different sides!
 *            - Mother side: @OneToMany(mappedBy = "mother") → "one" mother has
 *            "many" children
 *            - Child side: @ManyToOne → "many" children belongs to "one" mother
 *            - FK always stays in the "many" side (children table)
 */
@Entity
@Table(name = "children")
@Getter
@Setter
@NoArgsConstructor
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    /**
     * @ManyToOne → FK column ikkada untundi
     *            fetch = LAZY → Mother data need ayyinappudu maatrame load avutundi
     * @JoinColumn → "mother_id" column create chestundi children table lo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_id", nullable = false)
    private Mother mother;

    public Child(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
