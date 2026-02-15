package com.example.hibernateonetomanydemo.repository;

import com.example.hibernateonetomanydemo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
