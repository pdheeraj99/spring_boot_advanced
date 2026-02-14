package com.example.courseplatformdemo.dto;

public record CertificateTemplateResponse(
        Long id,
        String templateName,
        String headerText,
        String footerText) {
}