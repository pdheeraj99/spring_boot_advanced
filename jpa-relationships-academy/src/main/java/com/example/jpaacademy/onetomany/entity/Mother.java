package com.example.jpaacademy.onetomany.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================
 * 
 * @OneToMany Relationship — PARENT SIDE
 *            ============================================
 *
 *            Real-World Analogy: Mother → Children
 *            - Oka Mother ki MULTIPLE Children untaru
 *            - Oka Child ki exactly ONE Mother
 *
 *            IMPORTANT CONCEPTS:
 *
 *            1. @OneToMany(mappedBy = "mother") →
 *            - "children" list lo items oka CHILD lo unna "mother" field ki map
 *            avutayi
 *            - FK child table lo undi (mother_id column)
 *            - Mother table lo children column RADU!
 *
 *            2. cascade = ALL →
 *            - Mother save chesthe, children list lo unna children kuda
 *            auto-save avutayi
 *            - Mother delete chesthe, children kuda auto-delete avutayi
 *
 *            3. orphanRemoval = true →
 *            - mother.getChildren().remove(child) chesthe, child DB nunchi
 *            DELETE avutundi
 *            - Idi "orphan" — parent leni child ni remove chestundi
 *
 *            4. fetch = LAZY (DEFAULT for OneToMany!) →
 *            - Children data access chesthe maatrame SQL query fire avutundi
 *            - Performance saving — mother list load chesthe prathi mother ki
 *            children load avvadu
 *
 *            INTERVIEW QUESTION: "OneToMany default fetch type enti?"
 *            ANSWER: LAZY! (ManyToOne ki default EAGER)
 */
@Entity
@Table(name = "mothers")
@Getter
@Setter
@NoArgsConstructor
public class Mother {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    /**
     * mappedBy = "mother" → Child entity lo "mother" field owner
     * Children list initialize cheyyali (new ArrayList<>()) lekapothey
     * NullPointerException vastundi!
     */
    @OneToMany(mappedBy = "mother", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Child> children = new ArrayList<>();

    public Mother(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * CRITICAL: Helper method — BOTH SIDES synchronize cheyyali!
     * - children list lo add
     * - child.setMother(this) kuda set cheyyali
     * Lekapothey relationship properly persist avvadu!
     */
    public void addChild(Child child) {
        children.add(child);
        child.setMother(this);
    }

    public void removeChild(Child child) {
        children.remove(child);
        child.setMother(null);
    }
}
