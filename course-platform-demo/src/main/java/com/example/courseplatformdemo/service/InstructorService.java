package com.example.courseplatformdemo.service;

import com.example.courseplatformdemo.dto.CreateInstructorRequest;
import com.example.courseplatformdemo.dto.InstructorResponse;
import com.example.courseplatformdemo.entity.Instructor;
import com.example.courseplatformdemo.entity.InstructorProfile;
import com.example.courseplatformdemo.exception.ConflictException;
import com.example.courseplatformdemo.exception.ResourceNotFoundException;
import com.example.courseplatformdemo.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public InstructorResponse createInstructor(CreateInstructorRequest request) {
        instructorRepository.findByEmail(request.email()).ifPresent(i -> {
            throw new ConflictException("Instructor email already exists: " + request.email());
        });

        Instructor instructor = new Instructor();
        instructor.setName(request.name());
        instructor.setEmail(request.email());

        InstructorProfile profile = new InstructorProfile();
        profile.setHeadline(request.headline());
        profile.setExpertise(request.expertise());
        profile.setYearsExperience(request.yearsExperience());
        instructor.setProfile(profile);

        return dtoMapper.toInstructorResponse(instructorRepository.save(instructor));
    }

    @Transactional(readOnly = true)
    public Instructor getInstructorEntity(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + id));
    }
}