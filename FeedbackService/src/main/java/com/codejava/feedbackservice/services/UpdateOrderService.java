package com.codejava.feedbackservice.services;

import com.codejava.feedbackservice.entities.ValidationResponse;
import com.codejava.feedbackservice.repositories.OrderRepository;
import com.codejava.feedbackservice.repositories.ProductRepository;
import com.codejava.feedbackservice.repositories.ValidationResponseRepository;
import com.codejava.orderapp.entities.Product;
import com.codejava.orderapp.entities.User;
import com.codejava.orderapp.entities.order.Order;
import com.codejava.orderapp.entities.order.OrderItem;
import com.codejava.orderapp.entities.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UpdateOrderService {

    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
    public static final String PENDING = "PENDING";
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ValidationResponseRepository validationResponseRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ProductRepository productRepository;

    // Process validation response received from Kafka and save it
    public void processValidationResponse(Long orderId, String response) {
        String[] details = response.split("_");
        ValidationResponse validationResponse = ValidationResponse.builder()
                .orderId(orderId)
                .response(details[1])
                .typeOfValidation(details[0])
                .build();
        validationResponseRepository.save(validationResponse);
    }

    // Update order status based on validation responses
    public String updateOrderStatus(Long orderId) {
        List<ValidationResponse> responses = validationResponseRepository.findByOrderId(orderId);
        if (responses.size() <= 1) return PENDING;
        Order order = getOrderById(orderId);
        for (ValidationResponse response : responses) {
            if (!response.getResponse().equals(ACCEPTED)) {
                updateOrderStatus(order, OrderStatus.REJECTED);
                return REJECTED;
            }
        }
        // Update order status to ACCEPTED in the database
        updateOrderStatus(order, OrderStatus.ACCEPTED);
        return ACCEPTED;
    }

    // Update product stock after order is accepted
    public void updateStock(Long orderId) {
        Order order = getOrderById(orderId);
        List<OrderItem> items = order.getItems();
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

        }

    }

    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.getReferenceById(orderId);
    }

    // Send email notification to the customer about order status
    public void sendEmailToCustomer(Long orderId) {
        Order order = orderRepository.getReferenceById(orderId);
        User user = order.getUser();
        SimpleMailMessage message = new SimpleMailMessage();
        // Construct email body with order details
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear ").append(user.getName()).append(",\n\n");
        emailBody.append("Your order is : " + order.getStatus() + "\n\n");
        if (order.getStatus().equals(OrderStatus.REJECTED)) {
            List<ValidationResponse> validationResponses = validationResponseRepository.findByOrderId(orderId);
            for (ValidationResponse response : validationResponses) {
                if (response.getTypeOfValidation().equals("StockService") && response.getResponse().equals(REJECTED))
                    emailBody.append("We don't have enough products in stock to accept your order.").append("\n\n");
                if (response.getTypeOfValidation().equals("PaymentService") && response.getResponse().equals(REJECTED))
                    emailBody.append("Your bank account details are incorrect. Please try again to send the order with the correct bank account details").append("\n\n");
            }
        }
        // Assuming you have methods to retrieve order details such as order items, total price, etc.
        emailBody.append("Items: ");
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            emailBody.append(product.getName()).append(" : price : ");
            emailBody.append(product.getPrice());
            emailBody.append(" : quantity: ").append(orderItem.getQuantity()).append("\n");
        }
        emailBody.append("Total order amount: ").append(order.getTotalAmount()).append("\n\n");

        emailBody.append("Thank you for shopping with us!\n\n");
        emailBody.append("Sincerely,\n");
        emailBody.append("OrderApp");
        message.setText(emailBody.toString());
        message.setTo(user.getEmail());
        mailSender.send(message);
    }


}
