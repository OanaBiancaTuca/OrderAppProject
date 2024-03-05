# **Spring Kafka Order System**

### The Spring Kafka Order System is designed to provide a flexible and robust API for creating orders and processing their. The project is based on the microservice architecture and uses the Apache Kafka message broker to communicate between microservices. One of the main intentions of this project is to show how to use Apache Kafka in Spring Boot applications.

The project contains microservices: Order Service, Payment Service, Stock Service, and Feedback Service. All microservices are independent of each other.
#### order-service - it sends Order events to the Kafka topic
#### payment-service - it checks the validity of bank data to process the order
#### stock-service - it checks if the ordered products are available in stock.
#### feedback-service - it sends an email to the client with the status of the order: accepted or rejected.

## Here's the diagram illustrating project's architecture:
![Alt text](assets/diagram.png)
#### (1) The user post a new Order
#### (2) order-service sends a new Order with the status "PENDING".
#### 3) Upon receiving the Order, payment-service and stock-service process it by executing a local transaction based on the data.
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