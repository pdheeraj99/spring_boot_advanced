package com.example.hibernaterelationshipslab.service;

import com.example.hibernaterelationshipslab.dto.AppointmentResponse;
import com.example.hibernaterelationshipslab.dto.CreateDoctorRequest;
import com.example.hibernaterelationshipslab.dto.DoctorResponse;
import com.example.hibernaterelationshipslab.dto.SpecialtyResponse;
import com.example.hibernaterelationshipslab.entity.Doctor;
import com.example.hibernaterelationshipslab.entity.Specialty;
import com.example.hibernaterelationshipslab.exception.RelationshipConflictException;
import com.example.hibernaterelationshipslab.exception.ResourceNotFoundException;
import com.example.hibernaterelationshipslab.repository.DoctorRepository;
import com.example.hibernaterelationshipslab.repository.SpecialtyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorService(DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional
    public DoctorResponse createDoctor(CreateDoctorRequest request) {
        Doctor saved = doctorRepository.save(new Doctor(request.fullName(), request.licenseNumber()));
        return toDoctorResponse(saved);
    }

    @Transactional
    public DoctorResponse addSpecialty(Long doctorId, Long specialtyId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + doctorId));
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found: " + specialtyId));

        if (doctor.getSpecialties().contains(specialty)) {
            throw new RelationshipConflictException("Doctor already mapped to this specialty");
        }

        doctor.addSpecialty(specialty);
        Doctor saved = doctorRepository.save(doctor);
        return toDoctorResponse(saved);
    }

    @Transactional
    public void removeSpecialty(Long doctorId, Long specialtyId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + doctorId));
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found: " + specialtyId));

        if (!doctor.getSpecialties().contains(specialty)) {
            throw new RelationshipConflictException("Doctor is not mapped to this specialty");
        }

        doctor.removeSpecialty(specialty);
        doctorRepository.save(doctor);
    }

    @Transactional
    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        Doctor doctor = doctorRepository.findWithAppointmentsById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + doctorId));
        return doctor.getAppointments()
                .stream()
                .sorted(Comparator.comparing(a -> a.getAppointmentAt()))
                .map(PatientService::toAppointmentResponse)
                .toList();
    }

    public static DoctorResponse toDoctorResponse(Doctor doctor) {
        Set<SpecialtyResponse> specialties = doctor.getSpecialties()
                .stream()
                .map(s -> new SpecialtyResponse(s.getId(), s.getCode(), s.getName()))
                .collect(java.util.stream.Collectors.toSet());
        return new DoctorResponse(doctor.getId(), doctor.getFullName(), doctor.getLicenseNumber(), specialties);
    }
}
