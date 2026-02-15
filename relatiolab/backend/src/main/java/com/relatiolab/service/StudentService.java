package com.relatiolab.service;

import com.relatiolab.dto.request.CreateProfileRequest;
import com.relatiolab.dto.request.CreateStudentRequest;
import com.relatiolab.dto.response.StudentResponse;
import com.relatiolab.entity.Student;
import com.relatiolab.entity.StudentProfile;
import com.relatiolab.exception.ResourceNotFoundException;
import com.relatiolab.repository.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public StudentResponse create(CreateStudentRequest request) {
        Student student = new Student();
        student.setName(request.name());
        student.setEmail(request.email());
        return dtoMapper.toStudent(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse upsertProfile(Long studentId, CreateProfileRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        StudentProfile profile = student.getProfile() != null ? student.getProfile() : new StudentProfile();
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setLinkedinUrl(request.linkedinUrl());
        student.setProfile(profile);
        return dtoMapper.toStudent(studentRepository.save(student));
    }

    @Transactional
    public void deleteProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        student.setProfile(null);
        studentRepository.save(student);
    }

    @Transactional
    public List<StudentResponse> list() {
        return studentRepository.findAll().stream().map(dtoMapper::toStudent).toList();
    }

    @Transactional
    public StudentResponse get(Long id) {
        Student student = studentRepository.findWithGraphById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
        return dtoMapper.toStudent(student);
    }

    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found: " + id);
        }
        studentRepository.deleteById(id);
    }
}