package com.codejava.orderapp.services.order;

import com.codejava.orderapp.entities.BankAccount;
import com.codejava.orderapp.entities.User;
import com.codejava.orderapp.entities.order.Order;
import com.codejava.orderapp.entities.order.OrderItem;
import com.codejava.orderapp.entities.order.OrderStatus;
import com.codejava.orderapp.repositories.BankAccountRepository;
import com.codejava.orderapp.repositories.OrderItemRepository;
import com.codejava.orderapp.repositories.OrderRepository;
import com.codejava.orderapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    public Order placeOrder(Order order) {
        // Finding bank account by ID
        Optional<BankAccount> bankAccount = bankAccountRepository.findById(order.getBankAccount().getAccountId());
        List<OrderItem> orderItems = order.getItems();
        // Updating order items if they exist in the repository
        orderItems.replaceAll(item -> (orderItemRepository.findById(item.getOrderItem_id())).get());
        // Setting updated order items and calculating total amount
        order.setItems(orderItems);
        double totalAmount = calculateTotalAmount(order);
        order.setTotalAmount(totalAmount);
        // Setting bank account, order status, and user
        bankAccount.ifPresent(order::setBankAccount);
        order.setStatus(OrderStatus.PENDING);
        order.setUser(getCurrentUser());
        // Saving the order and returning it
        return orderRepository.save(order);
    }

    // Method to calculate total amount of an order
    private double calculateTotalAmount(Order order) {
        return order.getItems().stream().filter(orderItem -> orderItem.getProduct() != null).mapToDouble(orderItem -> orderItem.getProduct().getPrice() * orderItem.getQuantity()).sum();

    }

    // Method to get the current user
    public User getCurrentUser() {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Checking if user is authenticated
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            // Finding user by username
            user = userRepository.findByUsername(currentUserName).orElse(null);
        }
        return user;
    }
}
