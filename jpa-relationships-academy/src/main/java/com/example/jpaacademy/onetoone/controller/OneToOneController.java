package com.example.jpaacademy.onetoone.controller;

import com.example.jpaacademy.onetoone.dto.CreateHusbandRequest;
import com.example.jpaacademy.onetoone.dto.CreateWifeRequest;
import com.example.jpaacademy.onetoone.dto.HusbandResponse;
import com.example.jpaacademy.onetoone.service.OneToOneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onetoone")
public class OneToOneController {

    private final OneToOneService service;

    public OneToOneController(OneToOneService service) {
        this.service = service;
    }

    @PostMapping("/husbands")
    public ResponseEntity<HusbandResponse> createHusband(@Valid @RequestBody CreateHusbandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createHusband(request));
    }

    @PostMapping("/wives")
    public ResponseEntity<HusbandResponse> assignWife(@Valid @RequestBody CreateWifeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.assignWife(request));
    }

    @GetMapping("/husbands")
    public ResponseEntity<List<HusbandResponse>> getAllHusbands() {
        return ResponseEntity.ok(service.getAllHusbands());
    }

    @GetMapping("/husbands/{id}")
    public ResponseEntity<HusbandResponse> getHusband(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHusband(id));
    }

    @DeleteMapping("/husbands/{id}")
    public ResponseEntity<Void> deleteHusband(@PathVariable Long id) {
        service.deleteHusband(id);
        return ResponseEntity.noContent().build();
    }
}
