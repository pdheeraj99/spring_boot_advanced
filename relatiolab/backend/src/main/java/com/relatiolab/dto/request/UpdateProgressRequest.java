package com.relatiolab.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateProgressRequest(@Min(0) @Max(100) Integer progressPercent) {
}