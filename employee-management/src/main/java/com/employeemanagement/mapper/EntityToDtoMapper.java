package com.employeemanagement.mapper;

import com.employeemanagement.dto.*;
import com.employeemanagement.entity.Department;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Passport;
import com.employeemanagement.entity.Project;

import java.util.Collections;
import java.util.List;

public final class EntityToDtoMapper {

    private EntityToDtoMapper() {
    }

    public static EmployeeSummaryDto toEmployeeSummary(Employee employee) {
        return new EmployeeSummaryDto(employee.getId(), employee.getName(), employee.getEmail());
    }

    public static DepartmentDto toDepartmentDto(Department department) {
        List<EmployeeSummaryDto> employees = department.getEmployees() == null
                ? Collections.emptyList()
                : department.getEmployees().stream().map(EntityToDtoMapper::toEmployeeSummary).toList();

        return new DepartmentDto(department.getId(), department.getName(), department.getLocation(), employees);
    }

    public static PassportDto toPassportDto(Passport passport) {
        if (passport == null) {
            return null;
        }
        return new PassportDto(passport.getId(), passport.getPassportNumber(), passport.getIssuedCountry());
    }

    public static ProjectSummaryDto toProjectSummary(Project project) {
        return new ProjectSummaryDto(project.getId(), project.getName(), project.getDeadline());
    }

    public static ProjectDto toProjectDto(Project project) {
        List<EmployeeSummaryDto> employees = project.getEmployees() == null
                ? Collections.emptyList()
                : project.getEmployees().stream().map(EntityToDtoMapper::toEmployeeSummary).toList();

        return new ProjectDto(project.getId(), project.getName(), project.getDeadline(), employees);
    }

    public static EmployeeDetailDto toEmployeeDetailDto(Employee employee) {
        String departmentName = employee.getDepartment() != null ? employee.getDepartment().getName() : null;
        PassportDto passport = toPassportDto(employee.getPassport());
        List<ProjectSummaryDto> projects = employee.getProjects() == null
                ? Collections.emptyList()
                : employee.getProjects().stream().map(EntityToDtoMapper::toProjectSummary).toList();

        return new EmployeeDetailDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                departmentName,
                passport,
                projects
        );
    }
}
