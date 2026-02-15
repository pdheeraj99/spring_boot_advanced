package com.example.jpaacademy.onetomany.controller;

import com.example.jpaacademy.onetomany.dto.CreateChildRequest;
import com.example.jpaacademy.onetomany.dto.CreateMotherRequest;
import com.example.jpaacademy.onetomany.dto.MotherResponse;
import com.example.jpaacademy.onetomany.service.OneToManyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onetomany")
public class OneToManyController {

    private final OneToManyService service;

    public OneToManyController(OneToManyService service) {
        this.service = service;
    }

    @PostMapping("/mothers")
    public ResponseEntity<MotherResponse> createMother(@Valid @RequestBody CreateMotherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMother(request));
    }

    @PostMapping("/mothers/{motherId}/children")
    public ResponseEntity<MotherResponse> addChild(@PathVariable Long motherId,
            @Valid @RequestBody CreateChildRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addChild(motherId, request));
    }

    @GetMapping("/mothers")
    public ResponseEntity<List<MotherResponse>> getAllMothers() {
        return ResponseEntity.ok(service.getAllMothers());
    }

    @GetMapping("/mothers/{id}")
    public ResponseEntity<MotherResponse> getMother(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMother(id));
    }

    @DeleteMapping("/children/{childId}")
    public ResponseEntity<Void> deleteChild(@PathVariable Long childId) {
        service.deleteChild(childId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mothers/{id}")
    public ResponseEntity<Void> deleteMother(@PathVariable Long id) {
        service.deleteMother(id);
        return ResponseEntity.noContent().build();
    }
}
