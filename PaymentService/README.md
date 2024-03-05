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