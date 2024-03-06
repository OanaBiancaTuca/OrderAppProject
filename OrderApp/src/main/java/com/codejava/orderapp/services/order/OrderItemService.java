package com.codejava.orderapp.services.order;

import com.codejava.orderapp.entities.Product;
import com.codejava.orderapp.entities.User;
import com.codejava.orderapp.entities.order.Order;
import com.codejava.orderapp.entities.order.OrderItem;
import com.codejava.orderapp.repositories.OrderItemRepository;
import com.codejava.orderapp.repositories.ProductRepository;
import com.codejava.orderapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    UserRepository userRepository;

    @Autowired
    public OrderItemService(ProductRepository productRepository, OrderItemRepository orderItemRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        Optional<Product> product = productRepository.findById(orderItem.getProduct().getProductId());
        User currentUser = getCurrentUser();
        orderItem.setUser(currentUser);
        product.ifPresent(orderItem::setProduct);
        return orderItemRepository.save(orderItem);
    }

    // Method to delete an orderItem by ID
    public boolean deleteOrderItemById(Long orderItemId) {
        if (orderItemRepository.findById(orderItemId).isPresent()) {
            orderItemRepository.deleteById(orderItemId);
            return true;
        } else {
            return false;
        }
    }

    // Method to update an orderItem
    public OrderItem updateOrderItem(Long orderItemId, OrderItem updatedOrderItem) {
        // Check if the orderItem exists in the database
        Optional<OrderItem> existingOrderItemOptional = orderItemRepository.findById(orderItemId);
        if (existingOrderItemOptional.isPresent()) {
            // If the orderItem exists, update its fields and save it to the database
            OrderItem existingOrderItem = existingOrderItemOptional.get();
            existingOrderItem.setQuantity(updatedOrderItem.getQuantity());
            return orderItemRepository.save(existingOrderItem);
        } else {
            // Return null  to indicate that the orderItem was not found
            return null;
        }
    }

    public User getCurrentUser() {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            user = userRepository.findByUsername(currentUserName).orElse(null);
        }
        return user;
    }

    public List<OrderItem> getAllOrderItemsForCurrentUser(Long currentUserId) {
        return orderItemRepository.findByUserId(currentUserId);
    }

    public void updateOrderForOrderItem(Long orderItemId, Order order) {
        Optional<OrderItem> existingOrderItemOptional = orderItemRepository.findById(orderItemId);
        if (existingOrderItemOptional.isPresent()) {
            // If the orderItem exists, update its fields and save it to the database
            OrderItem existingOrderItem = existingOrderItemOptional.get();
            existingOrderItem.setOrder(order);
            orderItemRepository.save(existingOrderItem);

        }
    }
}
