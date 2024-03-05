# FeedbackService

#### This is the service that consumes messages from the feedback-topic sent by validation services, and stores them in a database. After that, it checks if both validations are accepted, updates the order status, stock, and notifies the customer. If one of the validations is incorrect: either the bank details are incorrect or the products are not available in stock, the order will be rejected, and the customer will be notified accordingly.

###### When a message is received on the topic, it will be processed and save as follows:

```
 Order order = orderRepository.getReferenceById(orderId);
        String[] details = response.split("_");
        ValidationResponse validationResponse = ValidationResponse.builder()
                .orderId(orderId)
                .response(details[1])
                .typeOfValidation(details[0])
                .build();
        validationResponseRepository.save(validationResponse);

```

###### This is how it looks in the database:

````
id      order_id        response           type_of_validation
1	    116         "ACCEPTED"	        "StockService"
2	    116	        "ACCEPTED"	        "PaymentService"
````

###### After verifying that both services have sent a response message with 'accepted', an email will be sent to the user.

###### Mail configuration:

```
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
```

###### Email for accepted order:

![Alt text](assets/email_orderaccepted.png)

###### Rejected order:

![Alt text](assets/email_orderRejected.png)