package com.example.jpaacademy.onetomany.service;

import com.example.jpaacademy.onetomany.dto.*;
import com.example.jpaacademy.onetomany.entity.Child;
import com.example.jpaacademy.onetomany.entity.Mother;
import com.example.jpaacademy.onetomany.repo.ChildRepo;
import com.example.jpaacademy.onetomany.repo.MotherRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OneToManyService {

    private final MotherRepo motherRepo;
    private final ChildRepo childRepo;

    public OneToManyService(MotherRepo motherRepo, ChildRepo childRepo) {
        this.motherRepo = motherRepo;
        this.childRepo = childRepo;
    }

    @Transactional
    public MotherResponse createMother(CreateMotherRequest request) {
        Mother mother = new Mother(request.name(), request.age());
        Mother saved = motherRepo.save(mother);
        return toResponse(saved);
    }

    @Transactional
    public MotherResponse addChild(Long motherId, CreateChildRequest request) {
        Mother mother = motherRepo.findById(motherId)
                .orElseThrow(() -> new RuntimeException("Mother not found: " + motherId));

        Child child = new Child(request.name(), request.age());
        mother.addChild(child); // Sets BOTH sides: adds to list + sets mother reference
        motherRepo.save(mother); // cascade = ALL → child auto-save!
        return toResponse(mother);
    }

    public List<MotherResponse> getAllMothers() {
        return motherRepo.findAllBy()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MotherResponse getMother(Long id) {
        Mother mother = motherRepo.findWithChildrenById(id)
                .orElseThrow(() -> new RuntimeException("Mother not found: " + id));
        return toResponse(mother);
    }

    @Transactional
    public void deleteChild(Long childId) {
        childRepo.deleteById(childId);
    }

    @Transactional
    public void deleteMother(Long id) {
        // cascade = ALL → children kuda auto-delete avutayi!
        motherRepo.deleteById(id);
    }

    private MotherResponse toResponse(Mother mother) {
        List<ChildResponse> children = mother.getChildren()
                .stream()
                .map(c -> new ChildResponse(c.getId(), c.getName(), c.getAge()))
                .toList();
        return new MotherResponse(mother.getId(), mother.getName(), mother.getAge(), children);
    }
}
