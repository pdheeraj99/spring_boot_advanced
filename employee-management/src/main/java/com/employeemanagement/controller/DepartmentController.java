package com.employeemanagement.controller;

import com.employeemanagement.dto.DepartmentDto;
import com.employeemanagement.entity.Department;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.mapper.EntityToDtoMapper;
import com.employeemanagement.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody Department department) {
        Department saved = departmentRepository.save(department);
        Department withEmployees = departmentRepository.findWithEmployeesById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Department not found"));
        return ResponseEntity.ok(EntityToDtoMapper.toDepartmentDto(withEmployees));
    }

    @PostMapping("/{deptId}/employees")
    public ResponseEntity<DepartmentDto> addEmployee(@PathVariable Long deptId, @RequestBody Employee employee) {
        Department department = departmentRepository.findWithEmployeesById(deptId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Department not found with id: " + deptId));

        department.addEmployee(employee);
        departmentRepository.save(department);

        Department updated = departmentRepository.findWithEmployeesById(deptId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Department not found with id: " + deptId));

        return ResponseEntity.ok(EntityToDtoMapper.toDepartmentDto(updated));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentRepository.findAllWithEmployees().stream()
                .map(EntityToDtoMapper::toDepartmentDto)
                .toList();
        return ResponseEntity.ok(departments);
    }
}

