package com.codejava.paymentservice.validations;

import com.codejava.orderapp.entities.BankAccount;

import java.time.LocalDate;
import java.time.YearMonth;

public class BankAccountValidation {
    public static Boolean validateAccount(BankAccount account) {
        boolean isValid = false;

        // card number must consist of exactly 16 digits.
        if (account.getCardNumber() != null && account.getCardNumber().matches("\\d{16}")) {
            // CVV must consist of exactly 3 digits.
            if ((account.getCvv() != null) && (String.valueOf(account.getCvv()).length() == 3)) {
                // accountHolderName and nameOnCard fields must not be null.
                if (account.getAccountHolderName() != null && account.getNameOnCard() != null) {
                    //IBAN in Romania consists of 24 characters:
                    //2 letter country code
                    //2 digit check number
                    //4 characters from the bank's bank code
                    //16 digit code for the bank account number

                    // Extract the parts of the IBAN
                    String iban = account.getIbanNumber();
                    String countryCode = iban.substring(0, 2);
                    String checkNumber = iban.substring(2, 4);
                    String bankCode = iban.substring(4, 8);
                    String accountNumber = iban.substring(8);

                    // Check if each part satisfies the requirements
                    if (countryCode.matches("[A-Z]{2}") &&
                            checkNumber.matches("\\d{2}") &&
                            bankCode.matches("[A-Za-z0-9]{4}") &&
                            accountNumber.matches("\\d{16}")) {
                        // must have a date greater than or equal to the day the order is made.
                        LocalDate currentDate = LocalDate.now();
                        YearMonth yearMonthExpiryDate = YearMonth.of(account.getExpiryYear(), account.getExpiryMonth());
                        if (yearMonthExpiryDate.isAfter(YearMonth.from(currentDate)) ||
                                yearMonthExpiryDate.equals(YearMonth.from(currentDate)))
                            isValid = true;

                    }
                }
            }
        }
        return isValid;
    }
}
