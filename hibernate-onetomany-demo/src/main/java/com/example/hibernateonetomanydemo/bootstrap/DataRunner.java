package com.example.hibernateonetomanydemo.bootstrap;

import com.example.hibernateonetomanydemo.entity.Order;
import com.example.hibernateonetomanydemo.entity.User;
import com.example.hibernateonetomanydemo.repository.OrderRepository;
import com.example.hibernateonetomanydemo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DemoReadService demoReadService;

    public DataRunner(UserRepository userRepository, OrderRepository orderRepository, DemoReadService demoReadService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.demoReadService = demoReadService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        System.out.println("--- 1:N Relationship Start ---");

        User user = new User("Dheeraj_Pilla");
        Order order1 = new Order("MacBook Pro");
        Order order2 = new Order("iPhone 15");
        Order order3 = new Order("Magic Mouse");

        user.addOrder(order1);
        user.addOrder(order2);
        user.addOrder(order3);

        User savedUser = userRepository.save(user);
        System.out.println("Saved User: " + savedUser.getUsername() + ", orders=" + savedUser.getOrders().size());

        demoReadService.printUserOrders(savedUser.getId());

        savedUser.removeOrder(savedUser.getOrders().get(0));
        userRepository.save(savedUser);
        System.out.println("After orphanRemoval, total orders in DB: " + orderRepository.count());

        System.out.println("--- 1:N Relationship End ---");
    }
}
