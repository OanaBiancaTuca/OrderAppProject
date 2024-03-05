package com.codejava.paymentservice.services;

import com.codejava.orderapp.entities.BankAccount;
import com.codejava.orderapp.entities.order.Order;
import com.codejava.paymentservice.validations.BankAccountValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@KafkaListener(topics = "order-topic", groupId = "payment-group")
public class PaymentService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // Constructor injection of KafkaTemplate
    private KafkaTemplate<Long, String> kafkaTemplate;

    public PaymentService(KafkaTemplate<Long, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Kafka message handler
    @KafkaHandler
    public void handle(Order order) throws ExecutionException, InterruptedException {
        LOGGER.info("Received a new event: " + order.getOrderId() + " from customer " +
                order.getUser().getName());
        // Get bank account details from the order
        BankAccount bankAccount = order.getBankAccount();
        String response = null;
        // Validate bank account
        if (BankAccountValidation.validateAccount(bankAccount)) {
            response = "ACCEPTED";
        } else {
            response = "REJECTED";
        }

        // Send a new message on topic feedback-topic with the validation result
        LOGGER.info("Send a new event: : " + order.getOrderId() + response);
        CompletableFuture<SendResult<Long, String>> result = kafkaTemplate.send("feedback-topic",
                order.getOrderId(),
                "PaymentService_" + response);

    }
}
