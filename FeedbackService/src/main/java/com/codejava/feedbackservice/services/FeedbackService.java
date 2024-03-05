package com.codejava.feedbackservice.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component

public class FeedbackService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final UpdateOrderService updateOrderService;

    public FeedbackService(UpdateOrderService updateOrderService) {
        this.updateOrderService = updateOrderService;
    }

    // Kafka message listener
    @KafkaListener(topics = "feedback-topic", groupId = "feedback-group")
    public void handle(ConsumerRecord<Long, String> response) {
        // Extracting orderId and response from the Kafka message
        Long orderId = response.key();

        LOGGER.info("Received a new event: " + orderId + " " + response.value());
        // Process validation response
        updateOrderService.processValidationResponse(orderId, response.value());
        if (updateOrderService.updateOrderStatus(orderId).equals("ACCEPTED")) {
            LOGGER.info("Order " + orderId + " is accepted!");
            //update stock
            updateOrderService.updateStock(orderId);

            //Uncomment this to send notifications via email, and also configure the username and password for sending emails
            // updateOrderService.sendEmailToCustomer(orderId);

        } else if (updateOrderService.updateOrderStatus(orderId).equals("REJECTED")) {
            LOGGER.info("Order " + orderId + "is rejected");

            //send email to inform user
            // updateOrderService.sendEmailToCustomer(orderId);

        } else {
            LOGGER.info("Order " + orderId + " is in pending..");
        }

    }
}
