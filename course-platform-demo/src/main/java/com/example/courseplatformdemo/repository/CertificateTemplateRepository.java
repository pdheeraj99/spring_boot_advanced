package com.example.courseplatformdemo.repository;

import com.example.courseplatformdemo.entity.CertificateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {
}