package com.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Passport = OWNING SIDE of OneToOne
// Enduku? Remember: "dependent entity holds the FK"
// Passport employee meedha depend avuthundi — employee lekunda passport meaningless
@Entity
@Table(name = "passports")
@Getter
@Setter
@NoArgsConstructor
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true ante oka passport_number oka sari matrame untundi
    @Column(name = "passport_number", nullable = false, unique = true)
    private String passportNumber;

    @Column(name = "issued_country")
    private String issuedCountry;

    // ===== THE ONE-TO-ONE RELATIONSHIP =====
    //
    // @OneToOne = "ee passport OKA employee ki matrame belong avuthundi"
    //
    // fetch = LAZY = "Employee data kaavalsinavappudu matrame DB nundi load
    // avuthundi"
    // (if EAGER, every time passport load chesthe employee kuda auto-load avuthundi
    // — wasteful!)
    //
    // @JoinColumn(name = "employee_id") = DB lo ee column create avuthundi as FK
    // idi Passport TABLE lo untundi — because Passport is OWNING SIDE
    //
    // nullable = false = "every passport ki employee undali, null koodadu"
    // unique = true = "oka employee ki oka passport matrame — duplicate FK koodadu"
    //
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;
}
