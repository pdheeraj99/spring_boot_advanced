package com.example.hibernaterelationshipslab.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "insurance_cards")
@Getter
@Setter
@NoArgsConstructor
public class InsuranceCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String providerName;

    @Column(nullable = false, unique = true)
    private String policyNumber;

    @Column(nullable = false)
    private LocalDate validTill;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    public InsuranceCard(String providerName, String policyNumber, LocalDate validTill) {
        this.providerName = providerName;
        this.policyNumber = policyNumber;
        this.validTill = validTill;
    }
}
