package com.example.hibernaterelationshipslab.repository;

import com.example.hibernaterelationshipslab.entity.Patient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @EntityGraph(attributePaths = {"insuranceCard", "appointments"})
    Optional<Patient> findWithInsuranceCardById(Long id);
}
