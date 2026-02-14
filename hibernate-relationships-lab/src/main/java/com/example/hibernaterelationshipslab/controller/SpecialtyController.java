package com.example.hibernaterelationshipslab.controller;

import com.example.hibernaterelationshipslab.dto.CreateSpecialtyRequest;
import com.example.hibernaterelationshipslab.dto.SpecialtyDoctorsResponse;
import com.example.hibernaterelationshipslab.dto.SpecialtyResponse;
import com.example.hibernaterelationshipslab.service.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> createSpecialty(@Valid @RequestBody CreateSpecialtyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyService.createSpecialty(request));
    }

    @GetMapping("/{specialtyId}/doctors")
    public ResponseEntity<SpecialtyDoctorsResponse> getDoctors(@PathVariable Long specialtyId) {
        return ResponseEntity.ok(specialtyService.getDoctors(specialtyId));
    }
}
