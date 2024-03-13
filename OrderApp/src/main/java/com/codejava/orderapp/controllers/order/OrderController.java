package com.codejava.orderapp.controllers.order;

import com.codejava.orderapp.entities.order.Order;
import com.codejava.orderapp.entities.order.OrderItem;
import com.codejava.orderapp.services.order.OrderItemService;
import com.codejava.orderapp.services.order.OrderService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Random;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final KafkaTemplate<Long, Order> kafkaTemplate;
    private final  OrderService orderService;
    private final OrderItemService orderItemService;


    @Autowired
    public OrderController(KafkaTemplate<Long, Order> kafkaTemplate, OrderService orderService, OrderItemService orderItemService) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }


    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        log.info("##### Received order ");
        Order savedOrder = orderService.placeOrder(order);
        if (savedOrder == null) {
            log.info("******** Message was send, but order is not valid");
            order.setOrderId(new Random().nextLong());
            kafkaTemplate.send("order-topic", order.getOrderId(), order);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request ...");
        }
        for (OrderItem orderItem : savedOrder.getItems()) {
            orderItemService.updateOrderForOrderItem(orderItem.getOrderItemId(), savedOrder);
        }

        //sending order to Kafka
        log.info("Before sending order to Kafka ");

        kafkaTemplate.send("order-topic", savedOrder.getOrderId(), savedOrder);

        log.info("******** Message was send");

        return ResponseEntity.status(HttpStatus.CREATED).body("Order in pending ...");

    }


}
