package com.example.hibernaterelationshipslab.repository;

import com.example.hibernaterelationshipslab.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("select distinct d from Doctor d " +
            "left join fetch d.appointments a " +
            "left join fetch a.patient " +
            "where d.id = :id")
    Optional<Doctor> findWithAppointmentsById(Long id);
}
