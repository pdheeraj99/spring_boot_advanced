package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.CreateStudentRequest;
import com.example.courseplatformdemo.dto.StudentResponse;
import com.example.courseplatformdemo.entity.Student;
import com.example.courseplatformdemo.entity.StudentProfile;
import com.example.courseplatformdemo.exception.ConflictException;
import com.example.courseplatformdemo.exception.ResourceNotFoundException;
import com.example.courseplatformdemo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        studentRepository.findByEmail(request.email()).ifPresent(s -> {
            throw new ConflictException("Student email already exists: " + request.email());
        });

        Student student = new Student();
        student.setName(request.name());
        student.setEmail(request.email());

        StudentProfile profile = new StudentProfile();
        profile.setBio(request.bio());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setDateOfBirth(request.dateOfBirth());
        student.setProfile(profile);

        return dtoMapper.toStudentResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse addBuddy(Long studentId, Long buddyId) {
        Student student = getStudentEntity(studentId);
        Student buddy = getStudentEntity(buddyId);
        student.addBuddy(buddy);
        studentRepository.save(student);
        return dtoMapper.toStudentResponse(student);
    }

    @Transactional(readOnly = true)
    public Student getStudentEntity(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
    }
}