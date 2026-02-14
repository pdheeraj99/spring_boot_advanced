package com.example.nplus1demo.repository;

import com.example.nplus1demo.entity.Order;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product")
    List<Order> findAllWithJoinFetch();

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderAudits WHERE o.id IN :orderIds")
    List<Order> fetchAuditsForOrders(@Param("orderIds") Set<Long> orderIds);
}
