package com.codejava.feedbackservice.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component

public class FeedbackService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UpdateOrderService updateOrderService;

    public FeedbackService(UpdateOrderService updateOrderService) {
        this.updateOrderService = updateOrderService;
    }

    // Kafka message listener
    @KafkaListener(topics = "feedback-topic", groupId = "feedback-group")
    public void handle(ConsumerRecord<Long, String> response) {
        // Extracting orderId and response from the Kafka message
        Long orderId = response.key();
        logger.info("Received a new event: %ld - %s", orderId, response.value());
        // Process validation response
        updateOrderService.processValidationResponse(orderId, response.value());
        if (updateOrderService.updateOrderStatus(orderId).equals("ACCEPTED")) {
            logger.info("Order {} is accepted!", orderId);
            //update stock
            updateOrderService.updateStock(orderId);
            //Uncomment this to send notifications via email, and also configure the username and password for sending emails
            // updateOrderService.sendEmailToCustomer(orderId);

        } else if (updateOrderService.updateOrderStatus(orderId).equals("REJECTED")) {
            logger.info("Order {} is rejected", orderId);

            //send email to inform user
            // updateOrderService.sendEmailToCustomer(orderId);

        } else {
            logger.info("Order {} is in pending..", orderId);
        }

    }
}
