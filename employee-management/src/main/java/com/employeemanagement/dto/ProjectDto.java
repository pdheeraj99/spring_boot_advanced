package com.employeemanagement.dto;

import java.time.LocalDate;
import java.util.List;

public record ProjectDto(Long id, String name, LocalDate deadline, List<EmployeeSummaryDto> employees) {
}
