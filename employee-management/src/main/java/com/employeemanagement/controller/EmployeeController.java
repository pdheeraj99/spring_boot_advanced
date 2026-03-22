package com.employeemanagement.controller;

import com.employeemanagement.dto.*;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Passport;
import com.employeemanagement.mapper.EntityToDtoMapper;
import com.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<EmployeeDetailDto> createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeRepository.save(employee);
        Employee withDetails = employeeRepository.findDetailsById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found with id: " + saved.getId()));
        return ResponseEntity.ok(EntityToDtoMapper.toEmployeeDetailDto(withDetails));
    }

    @PostMapping("/{id}/passport")
    public ResponseEntity<MessageDto> addPassport(@PathVariable Long id, @RequestBody Passport passport) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found with id: " + id));

        employee.setPassport(passport);
        employeeRepository.save(employee);

        return ResponseEntity.ok(new MessageDto("Passport added successfully via Cascade!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetailDto> getEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found with id: " + id));

        return ResponseEntity.ok(EntityToDtoMapper.toEmployeeDetailDto(employee));
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectSummaryDto>> getEmployeeProjects(@PathVariable Long id) {
        Employee employee = employeeRepository.findDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found with id: " + id));

        List<ProjectSummaryDto> projects = employee.getProjects().stream()
                .map(EntityToDtoMapper::toProjectSummary)
                .toList();

        return ResponseEntity.ok(projects);
    }
}

