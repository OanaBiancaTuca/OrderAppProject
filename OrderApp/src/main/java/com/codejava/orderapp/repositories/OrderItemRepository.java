package com.codejava.orderapp.repositories;

import com.codejava.orderapp.entities.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByUserId(Long currentUserId);
}
