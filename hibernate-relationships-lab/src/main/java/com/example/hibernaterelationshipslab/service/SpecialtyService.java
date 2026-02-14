package com.example.hibernaterelationshipslab.service;

import com.example.hibernaterelationshipslab.dto.CreateSpecialtyRequest;
import com.example.hibernaterelationshipslab.dto.DoctorSummaryResponse;
import com.example.hibernaterelationshipslab.dto.SpecialtyDoctorsResponse;
import com.example.hibernaterelationshipslab.dto.SpecialtyResponse;
import com.example.hibernaterelationshipslab.entity.Specialty;
import com.example.hibernaterelationshipslab.exception.ResourceNotFoundException;
import com.example.hibernaterelationshipslab.repository.SpecialtyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional
    public SpecialtyResponse createSpecialty(CreateSpecialtyRequest request) {
        Specialty saved = specialtyRepository.save(new Specialty(request.code(), request.name()));
        return toResponse(saved);
    }

    @Transactional
    public SpecialtyDoctorsResponse getDoctors(Long specialtyId) {
        Specialty specialty = specialtyRepository.findWithDoctorsById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found: " + specialtyId));

        List<DoctorSummaryResponse> doctors = specialty.getDoctors()
                .stream()
                .sorted(Comparator.comparing(d -> d.getId()))
                .map(d -> new DoctorSummaryResponse(d.getId(), d.getFullName(), d.getLicenseNumber()))
                .toList();

        return new SpecialtyDoctorsResponse(specialty.getId(), specialty.getCode(), specialty.getName(), doctors);
    }

    public static SpecialtyResponse toResponse(Specialty specialty) {
        return new SpecialtyResponse(specialty.getId(), specialty.getCode(), specialty.getName());
    }
}
