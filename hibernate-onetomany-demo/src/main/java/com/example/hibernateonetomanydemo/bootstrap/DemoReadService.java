package com.example.hibernateonetomanydemo.bootstrap;

import com.example.hibernateonetomanydemo.entity.Order;
import com.example.hibernateonetomanydemo.entity.User;
import com.example.hibernateonetomanydemo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoReadService {

    private final UserRepository userRepository;

    public DemoReadService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public void printUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        System.out.println("Fetched User: " + user.getUsername());
        System.out.println("Orders Count (LAZY loaded): " + user.getOrders().size());
        for (Order order : user.getOrders()) {
            System.out.println("Order -> " + order.getProductName());
        }
    }
}
