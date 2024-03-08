package com.codejava.paymentservice.validations;

import com.codejava.orderapp.entities.BankAccount;

import java.time.LocalDate;
import java.time.YearMonth;

public class BankAccountValidation {
    private static final String ACCEPTED ="ACCEPTED";
    private static final String REJECTED = "REJECTED";
    private BankAccountValidation() {}

    public static String validateAccount(BankAccount account) {
        // card number must consist of exactly 16 digits.
        if (!isValidCardNumber(account.getCardNumber())) return REJECTED;
        // CVV must consist of exactly 3 digits.
        if (!isValidCvv(account.getCvv())) return REJECTED;
        // accountHolderName and nameOnCard fields must not be null.
        if (!validHolderAndCustomer(account.getAccountHolderName(), account.getNameOnCard())) return REJECTED;
        //IBAN in Romania consists of 24 characters
        if (!isValidIban(account.getIbanNumber())) return REJECTED;
        // must have a date greater than or equal to the day the order is made.
        if (!isValidDate(account.getExpiryMonth(), account.getExpiryYear())) return REJECTED;
        return ACCEPTED;
    }

    private static boolean isValidDate(Integer expiryMonth, Integer expiryYear) {
        LocalDate currentDate = LocalDate.now();
        YearMonth yearMonthExpiryDate = YearMonth.of(expiryYear, expiryMonth);
        return (yearMonthExpiryDate.isAfter(YearMonth.from(currentDate)) ||
                yearMonthExpiryDate.equals(YearMonth.from(currentDate)));
    }

    private static boolean isValidIban(String iban) {
        //2 letter country code
        //2 digit check number
        //4 characters from the bank's bank code
        //16 digit code for the bank account number
        // Extract the parts of the IBAN
        String countryCode = iban.substring(0, 2);
        String checkNumber = iban.substring(2, 4);
        String bankCode = iban.substring(4, 8);
        String accountNumber = iban.substring(8);

        // Check if each part satisfies the requirements
        return (countryCode.matches("[A-Z]{2}") &&
                checkNumber.matches("\\d{2}") &&
                bankCode.matches("[A-Za-z0-9]{4}") &&
                accountNumber.matches("\\d{16}"));
    }


    public static boolean isValidCardNumber(String accountNumber) {
        return (accountNumber != null && accountNumber.matches("\\d{16}"));
    }

    public static boolean isValidCvv(int cvv) {
        return ((cvv > 0) && (String.valueOf(cvv).length() == 3));
    }

    public static boolean validHolderAndCustomer(String holderName, String customerName) {
        return (holderName != null && holderName.length() > 2 &&
                customerName != null && customerName.length() > 3);
    }
}
