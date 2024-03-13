package com.codejava.feedbackservice.services;

import com.codejava.feedbackservice.entities.ValidationResponse;
import com.codejava.feedbackservice.repositories.ValidationResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class FeedbackService {

    private final UpdateOrderService updateOrderService;

    private final ValidationResponseRepository validationResponseRepository;


    // Kafka message listener
    @KafkaListener(topics = "feedback-topic", groupId = "feedback-group")
    public void handle(ConsumerRecord<Long, String> response, Acknowledgment ak) {
        // Extracting orderId and response from the Kafka message
        Long orderId = response.key();
        log.info("Received a new event: {} - {}", orderId, response.value());
        //Check if the message was already processed before
        ValidationResponse existingResponse = validationResponseRepository.findByOrderIdAndTypeOfValidation
                (orderId, response.value().split("_")[0]);
        if (existingResponse != null) {
            log.info("Found a duplicate message id:{}", response.key());
            return;
        }
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
