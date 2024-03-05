package com.codejava.orderapp.controllers.order;

import com.codejava.orderapp.entities.order.OrderItem;
import com.codejava.orderapp.repositories.UserRepository;
import com.codejava.orderapp.services.order.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/basket")
public class OrderItemController {
    @Autowired
    OrderItemService orderItemService;

    @Autowired
    UserRepository userRepository;

    // Endpoint for creating an orderItem
    @PostMapping
    public ResponseEntity<String> createOrderItem(@RequestBody OrderItem orderItem) {

        try {
            OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);
            return ResponseEntity.status(HttpStatus.CREATED).body("OrderItem created successfully\n" + createdOrderItem.writeOrderItemDescription());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create OrderItem");
        }
    }

    // Endpoint for updating an orderItem
    @PutMapping("/{orderItemId}")
    public ResponseEntity<String> updateOrderItem(@PathVariable Long orderItemId, @RequestBody OrderItem updatedOrderItem) {
        OrderItem updatedItem = orderItemService.updateOrderItem(orderItemId, updatedOrderItem);
        if (updatedItem != null)
            return ResponseEntity.status(HttpStatus.OK).body("OrderItem updated successfully\n" + updatedItem.writeOrderItemDescription());
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OrderItem updated failed");
        }
    }

    // Endpoint for deleting an orderItem
    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable Long orderItemId) {
        boolean isDeleted = orderItemService.deleteOrderItemById(orderItemId);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK).body("OrderItem deleted successfully with ID: " + orderItemId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OrderItem not found with ID: " + orderItemId);
        }
    }

    // Endpoint for getting all order items for the current user
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItemsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        Long userId = userRepository.findByUsername(currentUserName).get().getId();
        List<OrderItem> orderItems = orderItemService.getAllOrderItemsForCurrentUser(userId);
        return ResponseEntity.ok(orderItems);
    }

}

