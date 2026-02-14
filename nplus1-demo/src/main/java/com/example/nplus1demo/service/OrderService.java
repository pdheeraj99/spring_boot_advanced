package com.example.nplus1demo.service;

import com.example.nplus1demo.dto.OrderAuditResponse;
import com.example.nplus1demo.dto.OrderItemResponse;
import com.example.nplus1demo.dto.OrderResponse;
import com.example.nplus1demo.dto.ProductResponse;
import com.example.nplus1demo.entity.Order;
import com.example.nplus1demo.entity.OrderItem;
import com.example.nplus1demo.repository.OrderRepository;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${demo.simulated-product-latency-ms:0}")
    private volatile long simulatedLatencyMs;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersWithNPlusOne() {
        Statistics stats = getAndResetStats();
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> result = orders.stream().map(this::toDtoWithLazyAccess).toList();
        log.info("N+1 path query executions={}", stats.getPrepareStatementCount());
        return result;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersOptimized() {
        Statistics stats = getAndResetStats();
        List<Order> orders = orderRepository.findAllWithJoinFetch();
        Set<Long> orderIds = orders.stream().map(Order::getId).collect(java.util.stream.Collectors.toSet());
        if (!orderIds.isEmpty()) {
            orderRepository.fetchAuditsForOrders(orderIds);
        }
        List<OrderResponse> result = orders.stream().map(this::toDto).toList();
        log.info("Optimized path query executions={}", stats.getPrepareStatementCount());
        return result;
    }

    private OrderResponse toDtoWithLazyAccess(Order order) {
        if (simulatedLatencyMs > 0) {
            try {
                Thread.sleep(simulatedLatencyMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return toDto(order);
    }

    private OrderResponse toDto(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream().map(this::toItemDto).toList();
        List<OrderAuditResponse> audits = order.getOrderAudits().stream()
                .map(a -> new OrderAuditResponse(a.getId(), a.getAction(), a.getTimestamp(), a.getUsername()))
                .toList();

        return new OrderResponse(order.getId(),
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getCustomerName(),
                items,
                audits);
    }

    private OrderItemResponse toItemDto(OrderItem item) {
        ProductResponse product = new ProductResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getDescription(),
                item.getProduct().getCategory());
        return new OrderItemResponse(item.getId(), item.getProductName(), item.getQuantity(), item.getPrice(), product);
    }

    private Statistics getAndResetStats() {
        Statistics stats = getStats();
        stats.clear();
        return stats;
    }

    private Statistics getStats() {
        return entityManagerFactory.unwrap(org.hibernate.SessionFactory.class).getStatistics();
    }

    public void setSimulatedLatencyMs(long simulatedLatencyMs) {
        this.simulatedLatencyMs = simulatedLatencyMs;
    }

    public long getSimulatedLatencyMs() {
        return simulatedLatencyMs;
    }
}
