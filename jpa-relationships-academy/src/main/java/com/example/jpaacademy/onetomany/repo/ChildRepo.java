package com.example.jpaacademy.onetomany.repo;

import com.example.jpaacademy.onetomany.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepo extends JpaRepository<Child, Long> {
}
