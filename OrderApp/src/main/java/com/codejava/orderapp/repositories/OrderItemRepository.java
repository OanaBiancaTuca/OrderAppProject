package com.codejava.orderapp.repositories;

import com.codejava.orderapp.entities.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findByUserId(Long currentUserId);
}
