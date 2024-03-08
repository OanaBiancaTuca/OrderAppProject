package com.codejava.stockservice.services;

import com.codejava.orderapp.entities.order.Order;
import com.codejava.stockservice.errors.NotRetryableException;
import com.codejava.stockservice.errors.RetryableException;
import com.codejava.stockservice.validations.StockValidation;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;


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
    public void handle(Order order) {
        LOGGER.info("Received a new event: " + order.getOrderId() + " from customer " +
                order.getUser().getName());
        try {
            // Validate stock
            // Determine response based on stock validation
            String response = StockValidation.validateStock(order.getItems());
            LOGGER.info(String.format("Send a new event: %s %s", order.getOrderId(), response));
            // Send a new message on topic feedback-topic with the validation result
            kafkaTemplate.send("feedback-topic", order.getOrderId(), "StockService_" + response);
            // Message template: "StockService_RESPONSE"

        } catch (ResourceAccessException ex) {
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
