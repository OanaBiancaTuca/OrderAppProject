# Stock Service

#### This is a stock data validation service. It reads a command from the order-topic, extracts the order details for products, and validates the stock.
#### After validation, send a response on feedback-topic :
```
StockService_REJECTED /
StockService_ACCEPTED
```