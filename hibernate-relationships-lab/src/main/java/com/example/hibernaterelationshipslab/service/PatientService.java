package com.example.hibernaterelationshipslab.service;

import com.example.hibernaterelationshipslab.dto.AppointmentResponse;
import com.example.hibernaterelationshipslab.dto.CreateInsuranceCardRequest;
import com.example.hibernaterelationshipslab.dto.CreatePatientRequest;
import com.example.hibernaterelationshipslab.dto.InsuranceCardResponse;
import com.example.hibernaterelationshipslab.dto.PatientResponse;
import com.example.hibernaterelationshipslab.entity.Appointment;
import com.example.hibernaterelationshipslab.entity.InsuranceCard;
import com.example.hibernaterelationshipslab.entity.Patient;
import com.example.hibernaterelationshipslab.exception.RelationshipConflictException;
import com.example.hibernaterelationshipslab.exception.ResourceNotFoundException;
import com.example.hibernaterelationshipslab.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {
        Patient saved = patientRepository.save(new Patient(request.fullName(), request.email()));
        return toPatientResponse(saved);
    }

    @Transactional
    public InsuranceCardResponse addInsuranceCard(Long patientId, CreateInsuranceCardRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        if (patient.getInsuranceCard() != null) {
            throw new RelationshipConflictException("Patient already has an insurance card");
        }

        InsuranceCard card = new InsuranceCard(request.providerName(), request.policyNumber(), request.validTill());
        patient.assignInsuranceCard(card);
        patientRepository.save(patient);
        return toInsuranceCardResponse(card);
    }

    @Transactional
    public PatientResponse getPatient(Long patientId) {
        Patient patient = patientRepository.findWithInsuranceCardById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        return toPatientResponse(patient);
    }

    public static PatientResponse toPatientResponse(Patient patient) {
        InsuranceCardResponse insuranceCard = patient.getInsuranceCard() == null
                ? null
                : toInsuranceCardResponse(patient.getInsuranceCard());
        List<AppointmentResponse> appointments = patient.getAppointments()
                .stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentAt))
                .map(PatientService::toAppointmentResponse)
                .toList();
        return new PatientResponse(patient.getId(), patient.getFullName(), patient.getEmail(), insuranceCard, appointments);
    }

    public static InsuranceCardResponse toInsuranceCardResponse(InsuranceCard card) {
        return new InsuranceCardResponse(card.getId(), card.getProviderName(), card.getPolicyNumber(), card.getValidTill());
    }

    public static AppointmentResponse toAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentAt(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getFullName()
        );
    }
}
