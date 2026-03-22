package com.employeemanagement.dto;

import java.time.LocalDate;

public record ProjectSummaryDto(Long id, String name, LocalDate deadline) {
}
