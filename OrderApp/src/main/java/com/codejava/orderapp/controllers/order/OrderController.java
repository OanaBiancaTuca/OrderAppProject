package com.codejava.orderapp.controllers.order;

import com.codejava.orderapp.entities.order.Order;
import com.codejava.orderapp.entities.order.OrderItem;
import com.codejava.orderapp.services.order.OrderItemService;
import com.codejava.orderapp.services.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    KafkaTemplate<Long, Order> kafkaTemplate;
    private OrderService orderService;
    private OrderItemService orderItemService;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(KafkaTemplate<Long, Order> kafkaTemplate, OrderService orderService, OrderItemService orderItemService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        LOGGER.info("##### Received order: {}", order);
        Order savedOrder = orderService.placeOrder(order);
        for (OrderItem orderItem : savedOrder.getItems()) {
            orderItemService.updateOrderForOrderItem(orderItem.getOrderItemId(), savedOrder);
        }

        //sending order to Kafka
        LOGGER.info("Before sending order to Kafka ");

        kafkaTemplate.send("order-topic", savedOrder.getOrderId(), savedOrder);

        LOGGER.info("******** Message was send");

        return ResponseEntity.status(HttpStatus.CREATED).body("Order in pending ...");

    }


}
