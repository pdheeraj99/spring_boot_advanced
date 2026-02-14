package com.example.hibernaterelationshipslab.repository;

import com.example.hibernaterelationshipslab.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    @Query("select distinct s from Specialty s left join fetch s.doctors where s.id = :id")
    Optional<Specialty> findWithDoctorsById(Long id);
}
