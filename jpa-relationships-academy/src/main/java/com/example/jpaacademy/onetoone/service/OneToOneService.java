package com.example.jpaacademy.onetoone.service;

import com.example.jpaacademy.onetoone.dto.*;
import com.example.jpaacademy.onetoone.entity.Husband;
import com.example.jpaacademy.onetoone.entity.Wife;
import com.example.jpaacademy.onetoone.repo.HusbandRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OneToOneService {

    private final HusbandRepo husbandRepo;

    public OneToOneService(HusbandRepo husbandRepo) {
        this.husbandRepo = husbandRepo;
    }

    @Transactional
    public HusbandResponse createHusband(CreateHusbandRequest request) {
        Husband husband = new Husband(request.name(), request.age());
        Husband saved = husbandRepo.save(husband);
        return toResponse(saved);
    }

    @Transactional
    public HusbandResponse assignWife(CreateWifeRequest request) {
        Husband husband = husbandRepo.findById(request.husbandId())
                .orElseThrow(() -> new RuntimeException("Husband not found: " + request.husbandId()));

        if (husband.getWife() != null) {
            throw new RuntimeException("Husband already has a wife! (OneToOne constraint)");
        }

        Wife wife = new Wife(request.name(), request.age());
        husband.assignWife(wife); // Helper method sets BOTH sides
        husbandRepo.save(husband); // cascade = ALL → wife auto-save avutundi!
        return toResponse(husband);
    }

    public List<HusbandResponse> getAllHusbands() {
        return husbandRepo.findAllBy()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public HusbandResponse getHusband(Long id) {
        Husband husband = husbandRepo.findWithWifeById(id)
                .orElseThrow(() -> new RuntimeException("Husband not found: " + id));
        return toResponse(husband);
    }

    @Transactional
    public void deleteHusband(Long id) {
        // cascade = ALL, orphanRemoval = true → wife kuda auto-delete avutundi!
        husbandRepo.deleteById(id);
    }

    private HusbandResponse toResponse(Husband husband) {
        WifeResponse wifeResp = husband.getWife() == null
                ? null
                : new WifeResponse(husband.getWife().getId(), husband.getWife().getName(), husband.getWife().getAge());
        return new HusbandResponse(husband.getId(), husband.getName(), husband.getAge(), wifeResp);
    }
}
