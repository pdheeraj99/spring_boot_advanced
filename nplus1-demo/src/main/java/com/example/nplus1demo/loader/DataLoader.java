package com.example.nplus1demo.loader;

import com.example.nplus1demo.entity.Order;
import com.example.nplus1demo.entity.OrderAudit;
import com.example.nplus1demo.entity.OrderItem;
import com.example.nplus1demo.entity.Product;
import com.example.nplus1demo.repository.OrderRepository;
import com.example.nplus1demo.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @PostConstruct
    public void load() {
        if (orderRepository.count() > 0) {
            return;
        }

        Random random = new Random(42);
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            Product p = new Product();
            p.setName("Product-" + i);
            p.setDescription("Description for product " + i);
            p.setCategory(i % 2 == 0 ? "Electronics" : "General");
            products.add(p);
        }
        products = productRepository.saveAll(products);

        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Order order = new Order();
            order.setOrderNumber("ORD-" + String.format("%04d", i));
            order.setOrderDate(LocalDate.now().minusDays(i));
            order.setCustomerName("Customer " + i);

            int items = 5 + random.nextInt(4);
            for (int j = 0; j < items; j++) {
                Product product = products.get(random.nextInt(products.size()));
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setProductName(product.getName());
                item.setQuantity(1 + random.nextInt(5));
                item.setPrice(BigDecimal.valueOf(10 + random.nextInt(90)));
                order.getOrderItems().add(item);
            }

            int audits = 3 + random.nextInt(3);
            for (int k = 0; k < audits; k++) {
                OrderAudit audit = new OrderAudit();
                audit.setOrder(order);
                audit.setAction(k % 2 == 0 ? "CREATED" : "UPDATED");
                audit.setTimestamp(Instant.now().minusSeconds((long) i * 60 + k));
                audit.setUsername("user" + ((k % 3) + 1));
                order.getOrderAudits().add(audit);
            }
            orders.add(order);
        }

        orderRepository.saveAll(orders);
        log.info("Seeded products={}, orders={}, items={}, audits={}",
                products.size(),
                orders.size(),
                orders.stream().mapToInt(o -> o.getOrderItems().size()).sum(),
                orders.stream().mapToInt(o -> o.getOrderAudits().size()).sum());
    }
}
