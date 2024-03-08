package com.codejava.paymentservice.services;

import com.codejava.orderapp.entities.BankAccount;
import com.codejava.orderapp.entities.order.Order;
import com.codejava.paymentservice.errors.NotRetryableException;
import com.codejava.paymentservice.errors.RetryableException;
import com.codejava.paymentservice.validations.BankAccountValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;


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
    public void handle(Order order) {
        LOGGER.info("Received a new event: " + order.getOrderId() + " from customer " +
                order.getUser().getName());
        try {
            // Get bank account details from the order
            BankAccount bankAccount = order.getBankAccount();
            // Validate bank account
            String response = BankAccountValidation.validateAccount(bankAccount);
            // Send a new message on topic feedback-topic with the validation result
            LOGGER.info("Send a new event: : " + order.getOrderId() + response);
            kafkaTemplate.send("feedback-topic", order.getOrderId(), "PaymentService_" + response);
        }catch (ResourceAccessException ex){
            LOGGER.error(ex.getMessage());
            throw new RetryableException(ex);
        } catch (HttpServerErrorException | NullPointerException ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }

    }
}
