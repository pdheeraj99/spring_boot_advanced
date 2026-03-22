package com.employeemanagement.controller;

import com.employeemanagement.dto.MessageDto;
import com.employeemanagement.dto.ProjectDto;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Project;
import com.employeemanagement.mapper.EntityToDtoMapper;
import com.employeemanagement.repository.EmployeeRepository;
import com.employeemanagement.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody Project project) {
        Project saved = projectRepository.save(project);
        Project withEmployees = projectRepository.findWithEmployeesById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found with id: " + saved.getId()));
        return ResponseEntity.ok(EntityToDtoMapper.toProjectDto(withEmployees));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = projectRepository.findAllWithEmployees().stream()
                .map(EntityToDtoMapper::toProjectDto)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        Project project = projectRepository.findWithEmployeesById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found with id: " + id));
        return ResponseEntity.ok(EntityToDtoMapper.toProjectDto(project));
    }

    @PostMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<MessageDto> linkEmployee(@PathVariable Long projectId, @PathVariable Long employeeId) {
        Project project = projectRepository.findWithEmployeesById(projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found with id: " + projectId));

        Employee employee = employeeRepository.findDetailsById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found with id: " + employeeId));

        boolean alreadyLinked = project.getEmployees().stream().anyMatch(e -> e.getId().equals(employeeId));
        if (!alreadyLinked) {
            project.addEmployee(employee);
            projectRepository.save(project);
            return ResponseEntity.ok(new MessageDto("Employee linked to project successfully"));
        }

        return ResponseEntity.ok(new MessageDto("Employee is already linked to this project"));
    }

    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<MessageDto> unlinkEmployee(@PathVariable Long projectId, @PathVariable Long employeeId) {
        Project project = projectRepository.findWithEmployeesById(projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found with id: " + projectId));

        Employee employee = employeeRepository.findDetailsById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found with id: " + employeeId));

        boolean linked = project.getEmployees().stream().anyMatch(e -> e.getId().equals(employeeId));
        if (linked) {
            project.removeEmployee(employee);
            projectRepository.save(project);
            return ResponseEntity.ok(new MessageDto("Employee unlinked from project successfully"));
        }

        return ResponseEntity.ok(new MessageDto("Employee is not linked to this project"));
    }
}

