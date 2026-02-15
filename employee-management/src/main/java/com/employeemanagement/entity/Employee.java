package com.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity — Hibernate ki cheptundi: "ee class ni database TABLE ga create chey"
// Result: "employees" ane table create avuthundi DB lo
@Entity
@Table(name = "employees")
@Getter // Lombok: automatic ga all fields ki getters generate avuthayi
@Setter // Lombok: automatic ga all fields ki setters generate avuthayi
@NoArgsConstructor // Lombok: empty constructor generate avuthundi (JPA ki mandatory)
public class Employee {

    // @Id = Primary Key
    // @GeneratedValue(IDENTITY) = auto-increment (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false ante DB lo NOT NULL constraint avuthundi
    @Column(nullable = false)
    private String name;

    // unique = true ante same email tho 2 employees create avvaru
    @Column(nullable = false, unique = true)
    private String email;

    // ===== ONE-TO-ONE: Employee ki oka Passport =====
    //
    // mappedBy = "employee" → "Passport.java lo 'employee' field already FK handle
    // chestundi.
    // Nenu just INVERSE SIDE ni — FK naa table lo raadu."
    //
    // cascade = ALL → Employee save chesthe passport kuda auto-save avuthundi
    // Employee delete chesthe passport kuda auto-delete avuthundi
    //
    // orphanRemoval = true → employee.setPassport(null) chesthe,
    // aa passport row DB lo nundi DELETE avuthundi!
    //
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Passport passport;

    // ===== Helper method: BIDIRECTIONAL SYNC =====
    // Passport set chesinapudu, passport ki kuda employee set avvali (BOTH SIDES!)
    // Idi cheyakapothe: memory lo inconsistent state — bugs vasthay
    public void setPassport(Passport passport) {
        if (passport == null) {
            // passport remove chesthe, old passport lo employee null chey
            if (this.passport != null) {
                this.passport.setEmployee(null);
            }
        } else {
            // new passport ki this employee set chey
            passport.setEmployee(this);
        }
        this.passport = passport;
    }
}
