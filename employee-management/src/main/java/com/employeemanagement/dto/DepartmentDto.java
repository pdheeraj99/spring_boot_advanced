package com.employeemanagement.dto;

import java.util.List;

public record DepartmentDto(Long id, String name, String location, List<EmployeeSummaryDto> employees) {
}
