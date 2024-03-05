package com.codejava.orderapp.controllers;

import com.codejava.orderapp.entities.BankAccount;
import com.codejava.orderapp.services.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Validated
public class BankAccountController {
    @Autowired
    BankAccountService bankService;

    @PostMapping("")
    public ResponseEntity<String> addAccount(@Valid @RequestBody BankAccount account) {
        String responseValidate = bankService.validateBankAccount(account);
        if (responseValidate.equals("OK")) {
            BankAccount savedAccount = bankService.addAccount(account);
            return ResponseEntity.status(HttpStatus.CREATED).body("Account is successfully saved ");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseValidate);
        }
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> getAllAccounts() {
        return ResponseEntity.status(HttpStatus.OK).body(bankService.getAllAccounts());
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        try {
            bankService.deleteAccount(accountId);
            return ResponseEntity.status(HttpStatus.OK).body("Account deleted!");
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found!");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found!");
        }
    }

}


