# Stock Service

#### This is a stock data validation service. It reads a command from the order-topic, extracts the order details for products, and validates the stock.
#### After validation, send a response on feedback-topic :
```
StockService_REJECTED /
StockService_ACCEPTED
```

##### Error Handling and Dead Letter Topic (DLT) Strategy:

- Utilizes Spring's DefaultErrorHandler to handle errors that occur during message consumption by Kafka listeners.
- Errors are retried up to a maximum of 3 times, with a delay of 5000 milliseconds between retries.
- Certain exceptions, such as NotRetryableException and RetryableException, are explicitly marked for retry or non-retry
  based on application logic.
- Implements a Dead Letter Topic (DLT) strategy for handling messages that cannot be successfully processed after
  retries.
- Uses DeadLetterPublishingRecoverer to move failed messages to a separate topic for further analysis or manual
  processing.

***Exception Handling:***

- In case of a network exception (ResourceAccessException), the service will attempt to retry processing the order as
  this is considered a transient error.
- If a NullPointerException occurs, it indicates that the order received does not have products associated. This
  situation is treated as a non-retryable error since it requires manual intervention or further investigation to
  resolve.
- Other exceptions, such as HTTP-related errors (HttpServerErrorException), are also treated as non-retryable errors and
  will not be retried.
- All other exceptions are handled similarly as non-retryable errors and will not be retried.

## Same logic as in PaymentService: look here:
[PaymentService](https://github.com/OanaBiancaTuca/OrderAppProject/blob/master/PaymentService/README.md)