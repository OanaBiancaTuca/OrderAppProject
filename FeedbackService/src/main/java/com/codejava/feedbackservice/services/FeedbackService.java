package com.codejava.feedbackservice.services;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class FeedbackService {

    private final UpdateOrderService updateOrderService;

    public FeedbackService(UpdateOrderService updateOrderService) {
        this.updateOrderService = updateOrderService;
    }

    // Kafka message listener
    @KafkaListener(topics = "feedback-topic", groupId = "feedback-group")
    public void handle(ConsumerRecord<Long, String> response,  Acknowledgment ak) {
        // Extracting orderId and response from the Kafka message
        Long orderId = response.key();
        log.info("Received a new event: {} - {}", orderId, response.value());
        // Process validation response
        updateOrderService.processValidationResponse(orderId, response.value());
        if (updateOrderService.updateOrderStatus(orderId).equals("ACCEPTED")) {
            log.info("Order {} is accepted!", orderId);
            //update stock
            updateOrderService.updateStock(orderId);
            //Uncomment this to send notifications via email, and also configure the username and password for sending emails
            // updateOrderService.sendEmailToCustomer(orderId);
            log.info("Order {} is accepted!", orderId);
            ak.acknowledge();
        } else if (updateOrderService.updateOrderStatus(orderId).equals("REJECTED")) {
            log.info("Order {} is rejected", orderId);

            //send email to inform user
            // updateOrderService.sendEmailToCustomer(orderId);
            ak.acknowledge();
        } else {
            log.info("Order {} is in pending..", orderId);
            ak.acknowledge();
        }

    }
}
