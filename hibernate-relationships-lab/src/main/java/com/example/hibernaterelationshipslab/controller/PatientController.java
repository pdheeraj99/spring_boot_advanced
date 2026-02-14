package com.example.hibernaterelationshipslab.controller;

import com.example.hibernaterelationshipslab.dto.CreateInsuranceCardRequest;
import com.example.hibernaterelationshipslab.dto.CreatePatientRequest;
import com.example.hibernaterelationshipslab.dto.InsuranceCardResponse;
import com.example.hibernaterelationshipslab.dto.PatientResponse;
import com.example.hibernaterelationshipslab.service.PatientService;
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
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request));
    }

    @PostMapping("/{patientId}/insurance-card")
    public ResponseEntity<InsuranceCardResponse> addInsuranceCard(@PathVariable Long patientId,
                                                                  @Valid @RequestBody CreateInsuranceCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.addInsuranceCard(patientId, request));
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatient(patientId));
    }
}
