package com.example.hibernaterelationshipslab.repository;

import com.example.hibernaterelationshipslab.entity.InsuranceCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceCardRepository extends JpaRepository<InsuranceCard, Long> {
}
