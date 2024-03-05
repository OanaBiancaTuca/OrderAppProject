# **Spring Kafka Order System**

### The Spring Kafka Order System is designed to provide a flexible and robust API for creating orders and processing their. The project is based on the microservice architecture and uses the Apache Kafka message broker to communicate between microservices. One of the main intentions of this project is to show how to use Apache Kafka in Spring Boot applications.

The project contains microservices: Order Service, Payment Service, Stock Service, and Feedback Service. All
microservices are independent of each other.

#### order-service - it sends Order events to the Kafka topic

#### payment-service - it checks the validity of bank data to process the order

#### stock-service - it checks if the ordered products are available in stock.

#### feedback-service - it sends an email to the client with the status of the order: accepted or rejected.

## Here's the diagram illustrating project's architecture:

![Alt text](assets/diagram.png)

#### (1) The user post a new Order

#### (2) order-service sends a new Order with the status "PENDING".

#### (3) Upon receiving the Order, payment-service and stock-service process it by executing a local transaction based on the data.

#### (4) After processing, payment-service and stock-service respond with the Order's status as "ACCEPTED" or "REJECTED".

#### (5) The feedback-service handles incoming streams of orders from the payment-service and stock-service. It associates them by Order ID .

#### (6) feedback-service sends Feedback to user

## Domain model:

![Alt text](assets/dbSchema.png)

## API's for Order-service

#### Examples of request/response:

#### For adding/removing products, you will need to log in using an admin account.

#### POST : http://localhost:8080/auth/login

```
{
    "username":"admin",
    "password":"admin"
}
```

Response:

````
{
    "user": {
        "id": 4,
        "username": "admin",
        "password": "$2a$10$ePE83VY6czeab5gpUvJTPezX7welsTH8KrjeTuoLb7fx8aWbFANPy",
        "email": "admin@gmail.com",
        "name": "admin",
        "address": "",
        "phoneNumber": "",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "accountNonLocked": true
    },
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwOTAyNzAxOCwiZXhwIjoxNzA5MDI4ODE4fQ.vU59nCMlhitG9ThvOTXzvUcEWACP7pdeS04Zmz4OhvI"
}
````

#### For add new product: POST http://localhost:8080/products

You need to include the JWT received at login in the Authorization header as follows:
![Alt text](assets/authorizationAdmin.png)
Example of request for creating new product:

````
{
"name":"Iphone 11",
"price":3000,
"quantity":20
}
````

#### For delete product: DELETE http://localhost:8080/products/idProduct

#### For registration: POST : http://localhost:8080/auth/register

Example of request for creating user with ROLE_USER:

```
{
"username":"userOana",
"password":"user",
"email":"user@gmail.com",
"name":"Oana Tuca",
"address":"Bucharest",
"phoneNumber":"0741000000"
}
```

#### For login: POST:http://localhost:8080/auth/login

```
{
    "username":"userOana",
    "password":"user"
}
```

##### Response:

```
{
    "user": {
        "id": 1,
        "username": "userOana",
        "password": "$2a$10$k7wfW3c2J7dYAdPgzskgCu2oH8gITjTjKmqNaU8VDcWT1VCnRC/.W",
        "email": "user@gmail.com",
        "name": "Oana Tuca",
        "address": "Bucharest",
        "phoneNumber": "0741000000",
        "enabled": true,
        "accountNonLocked": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true
    },
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyT2FuYSIsImlhdCI6MTcwOTAyNjA1MywiZXhwIjoxNzA5MDI3ODUzfQ.azWBTmKHu1OueYk-LF36zrLRLKp1uEsAFiREO3hUu3Q"
}
```

#### For GET all Products: GET http://localhost:8080/products

Response:

```
[
    {
        "productId": 1,
        "name": "Iphone 11",
        "price": 3000.0,
        "quantity": 20
    },
    {
        "productId": 2,
        "name": "Iphone 14 Pro Max",
        "price": 7000.0,
        "quantity": 5
    },
    {
        "productId": 3,
        "name": "Apple Watch 9 ",
        "price": 2000.0,
        "quantity": 3
    }
]
```

#### Add a bank account: POST http://localhost:8080/accounts

```
{
    "cardNumber": "123456789",
    "cvv":123,
    "expiryMonth":12,
    "expiryYear":2024,
    "accountHolderName":"BCR",
    "ibanNumber":"RO49AAAA1B31007593840000",
    "nameOnCard":"Oana"
    
}
```

#### GET all bank accounts: http://localhost:8080/accounts

#### DELETE bank account: http://localhost:8080/accounts/accountId

#### For adding items to the basket: POST http://localhost:8080/basket

Example of request:

```
{
    "product":{
        "productId":1
    },
    "quantity":1
}
```

#### GET all products from the basket:  http://localhost:8080/basket

Response:

````
[
    {
        "orderItem_id": 1,
        "product": {
            "productId": 1,
            "name": "Iphone 11",
            "price": 3000.0,
            "quantity": 20
        },
        "quantity": 1
    },
    {
        "orderItem_id": 2,
        "product": {
            "productId": 3,
            "name": "Apple Watch 9 ",
            "price": 2000.0,
            "quantity": 3
        },
        "quantity": 1
    }
]
````

#### DELETE product from the basket:http://localhost:8080/basket/itemId

#### POST ORDER: http://localhost:8080/orders

Request:

```
{
    "items":[
        { "orderItem_id":1},
         { "orderItem_id":2}
        
        ],
    "bankAccount":{
        "accountId":1
    }
}
```

Response:
``
Order in pending ...Order{orderId=1, totalAmount=5000.0}
``

Message sent to Kafka:

```
{"orderId":19,"items":[{"orderItem_id":1,"product":{"productId":1,"name":"Iphone 11","price":3000.0,"quantity":20},"quantity":1},{"orderItem_id":2,"product":{"productId":3,"name":"Apple Watch 9 ","price":2000.0,"quantity":3},"quantity":1}],"user":{"id":1,"username":"userOana","password":"$2a$10$k7wfW3c2J7dYAdPgzskgCu2oH8gITjTjKmqNaU8VDcWT1VCnRC/.W","email":"user@gmail.com","name":"Oana Tuca","address":"Bucharest","phoneNumber":"0741000000","enabled":true,"credentialsNonExpired":true,"accountNonExpired":true,"accountNonLocked":true},"bankAccount":{"accountId":1,"cardNumber":"123456789","cvv":123,"expiryMonth":12,"expiryYear":2024,"nameOnCard":"Oana","accountHolderName":"BCR","ibanNumber":"RO49AAAA1B31007593840000"},"totalAmount":5000.0,"status":"PENDING"}

```

# Payment Service

#### This is a bank data validation service. It reads a command from the order-topic, extracts the bank account details, and validates them as follows:

1. card number must consist of exactly 16 digits.
2. CVV must consist of exactly 3 digits.
3. accountHolderName and nameOnCard fields must not be null.
4. IBAN in Romania consists of 24 characters:

- 2 letter country code
- 2 digit check number
- 4 characters from the bank's bank code
- 16 digit code for the bank account number

5. must have a date greater than or equal to the day the order is made.

#### After validations, the payment service will send a message on the feedback-topic with such a response.

```
PaymentService_REJECTED /
PaymentService_ACCEPTED
```

# Stock Service

#### This is a stock data validation service. It reads a command from the order-topic, extracts the order details for products, and validates the stock.

#### After validation, send a response on feedback-topic :

```
StockService_REJECTED /
StockService_ACCEPTED
```

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