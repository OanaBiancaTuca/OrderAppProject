package com.codejava.stockservice.services;

import com.codejava.orderapp.entities.order.Order;
import com.codejava.stockservice.validations.StockValidation;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Component
@KafkaListener(topics = "order-topic", groupId = "stock-group")
public class StockService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private KafkaTemplate<Long, String> kafkaTemplate;

    public StockService(KafkaTemplate<Long, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Kafka message handler
    @KafkaHandler
    public void handle(Order order) throws ExecutionException, InterruptedException {
        LOGGER.info("Received a new event: " + order.getOrderId() + " from customer " +
                order.getUser().getName());
        // Validate stock
        // Determine response based on stock validation
        String response = null;
        if (StockValidation.validateStock(order.getItems())) {
            response = "ACCEPTED";
        } else {
            response = "REJECTED";
        }

        LOGGER.info("Send a new event: : " + order.getOrderId() + response);
        // Send a new message on topic feedback-topic with the validation result
        CompletableFuture<SendResult<Long, String>> result = kafkaTemplate.send("feedback-topic",
                order.getOrderId(), "StockService_" + response);
        // Message template: "StockService_RESPONSE"



    }
}
