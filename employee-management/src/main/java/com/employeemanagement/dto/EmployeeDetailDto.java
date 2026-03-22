package com.employeemanagement.dto;

import java.util.List;

public record EmployeeDetailDto(
        Long id,
        String name,
        String email,
        String departmentName,
        PassportDto passport,
        List<ProjectSummaryDto> projects
) {
}
