package com.example.hibernateonetoonedemo.repository;

import com.example.hibernateonetoonedemo.entity.Wife;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WifeRepository extends JpaRepository<Wife, Long> {
}
