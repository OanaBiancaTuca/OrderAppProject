package com.codejava.feedbackservice.services;

import com.codejava.feedbackservice.repositories.OrderRepository;
import com.codejava.orderapp.entities.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}