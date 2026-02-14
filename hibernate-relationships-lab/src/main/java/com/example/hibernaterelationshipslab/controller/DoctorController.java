package com.example.hibernaterelationshipslab.controller;

import com.example.hibernaterelationshipslab.dto.AppointmentResponse;
import com.example.hibernaterelationshipslab.dto.CreateDoctorRequest;
import com.example.hibernaterelationshipslab.dto.DoctorResponse;
import com.example.hibernaterelationshipslab.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(request));
    }

    @PostMapping("/{doctorId}/specialties/{specialtyId}")
    public ResponseEntity<DoctorResponse> addSpecialty(@PathVariable Long doctorId, @PathVariable Long specialtyId) {
        return ResponseEntity.ok(doctorService.addSpecialty(doctorId, specialtyId));
    }

    @DeleteMapping("/{doctorId}/specialties/{specialtyId}")
    public ResponseEntity<Void> removeSpecialty(@PathVariable Long doctorId, @PathVariable Long specialtyId) {
        doctorService.removeSpecialty(doctorId, specialtyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{doctorId}/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorAppointments(doctorId));
    }
}
