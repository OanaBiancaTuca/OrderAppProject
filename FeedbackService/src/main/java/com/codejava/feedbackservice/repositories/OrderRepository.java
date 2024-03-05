package com.codejava.feedbackservice.repositories;

import com.codejava.orderapp.entities.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
    }
