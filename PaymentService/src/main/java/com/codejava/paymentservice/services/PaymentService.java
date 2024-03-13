package com.codejava.paymentservice.services;

import com.codejava.orderapp.entities.BankAccount;
import com.codejava.orderapp.entities.order.Order;
import com.codejava.paymentservice.errors.NotRetryableException;
import com.codejava.paymentservice.errors.RetryableException;
import com.codejava.paymentservice.validations.BankAccountValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;


@Component
@KafkaListener(topics = "order-topic", groupId = "payment-group")
@Slf4j
public class PaymentService {


    // Constructor injection of KafkaTemplate
    private  final KafkaTemplate<Long, String> kafkaTemplate;

    public PaymentService(KafkaTemplate<Long, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Kafka message handler
    @KafkaHandler
    public void handle(Order order) {
        log.info("Received a new event: " + order.getOrderId() + " from customer " +
                order.getUser().getName());
        try {
            // Get bank account details from the order
            BankAccount bankAccount = order.getBankAccount();
            // Validate bank account
            String response = BankAccountValidation.validateAccount(bankAccount);
            // Send a new message on topic feedback-topic with the validation result
            log.info("Send a new event: : " + order.getOrderId() + response);
            kafkaTemplate.send("feedback-topic", order.getOrderId(), "PaymentService_" + response);
        } catch (ResourceAccessException ex) {
            log.error(ex.getMessage());
            throw new RetryableException(ex);
        } catch (HttpServerErrorException | NullPointerException ex) {
            log.error(ex.getMessage());
            throw new NotRetryableException(ex);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }

    }
}
