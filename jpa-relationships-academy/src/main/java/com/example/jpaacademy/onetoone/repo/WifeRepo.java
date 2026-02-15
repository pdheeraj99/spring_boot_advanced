package com.example.jpaacademy.onetoone.repo;

import com.example.jpaacademy.onetoone.entity.Wife;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WifeRepo extends JpaRepository<Wife, Long> {
}
