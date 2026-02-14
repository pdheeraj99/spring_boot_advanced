package com.example.hibernateonetoonedemo.repository;

import com.example.hibernateonetoonedemo.entity.Husband;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HusbandRepository extends JpaRepository<Husband, Long> {
}
