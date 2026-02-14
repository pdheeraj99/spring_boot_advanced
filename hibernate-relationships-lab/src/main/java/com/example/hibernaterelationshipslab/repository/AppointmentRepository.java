package com.example.hibernaterelationshipslab.repository;

import com.example.hibernaterelationshipslab.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
