package com.example.hibernateonetomanydemo.repository;

import com.example.hibernateonetomanydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
