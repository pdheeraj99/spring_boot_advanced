package com.example.jpaacademy.onetoone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ============================================
 * 
 * @OneToOne Relationship — PARENT SIDE
 *           ============================================
 *
 *           Real-World Analogy: Husband ↔ Wife
 *           - Oka Husband ki exactly ONE Wife untundi
 *           - Oka Wife ki exactly ONE Husband untadi
 *
 *           IMPORTANT CONCEPTS:
 *           1. mappedBy = "husband" → Wife entity lo "husband" field ki map
 *           avutundi
 *           (Wife is the OWNING SIDE because it holds the FK)
 *           2. cascade = ALL → Husband save chesthe Wife kuda auto-save
 *           avutundi
 *           3. orphanRemoval = true → Husband nunchi wife remove chesthe, wife
 *           DB nunchi delete avutundi
 *           4. fetch = LAZY → Wife data NEED ayyinappudu maatrame load avutundi
 *           (performance best practice)
 */
@Entity
@Table(name = "husbands")
@Getter
@Setter
@NoArgsConstructor
public class Husband {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    /**
     * mappedBy = "husband" ante:
     * - "Nenu parent side, but FK Wife table lo undi"
     * - Wife entity lo "husband" field owner (FK holder)
     * - Husband table lo wife_id column RADU!
     */
    @OneToOne(mappedBy = "husband", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Wife wife;

    public Husband(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * Helper method — BOTH sides set cheyyadam important!
     * husband.setWife(wife) + wife.setHusband(husband) — BOTH kavali
     * Lekapothey Hibernate relationship properly track cheyyadu
     */
    public void assignWife(Wife wife) {
        this.wife = wife;
        if (wife != null) {
            wife.setHusband(this);
        }
    }
}
