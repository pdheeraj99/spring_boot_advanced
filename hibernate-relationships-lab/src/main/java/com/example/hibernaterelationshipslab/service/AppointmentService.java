package com.example.hibernaterelationshipslab.service;

import com.example.hibernaterelationshipslab.dto.AppointmentResponse;
import com.example.hibernaterelationshipslab.dto.CreateAppointmentRequest;
import com.example.hibernaterelationshipslab.entity.Appointment;
import com.example.hibernaterelationshipslab.entity.Doctor;
import com.example.hibernaterelationshipslab.entity.Patient;
import com.example.hibernaterelationshipslab.exception.ResourceNotFoundException;
import com.example.hibernaterelationshipslab.repository.AppointmentRepository;
import com.example.hibernaterelationshipslab.repository.DoctorRepository;
import com.example.hibernaterelationshipslab.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + request.doctorId()));
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.patientId()));

        Appointment appointment = new Appointment(request.appointmentAt(), request.reason());
        doctor.addAppointment(appointment);
        patient.addAppointment(appointment);

        Appointment saved = appointmentRepository.save(appointment);
        return PatientService.toAppointmentResponse(saved);
    }
}
