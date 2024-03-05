package com.codejava.orderapp.services;

import com.codejava.orderapp.entities.BankAccount;
import com.codejava.orderapp.entities.User;
import com.codejava.orderapp.repositories.BankAccountRepository;
import com.codejava.orderapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankAccountService {


    @Autowired
    private BankAccountRepository bankRepository;

    @Autowired
    private UserRepository userRepository;


    public BankAccount addAccount(BankAccount account) {
        User user = getCurrentUser();
        account.setUser(user);
        return bankRepository.save(account);
    }

    public void deleteAccount(Long accountId) throws AccountNotFoundException, UsernameNotFoundException {
        BankAccount bankAccount = bankRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        User user = getCurrentUser();
        if (!bankAccount.getUser().equals(user)) {
            throw new AccountNotFoundException();
        }
        bankRepository.delete(bankAccount);
    }

    public List<BankAccount> getAllAccounts() {
        User user = getCurrentUser();
        List<BankAccount> userAccounts = new ArrayList<>();
        List<BankAccount> allAccounts = bankRepository.findAll();
        for (BankAccount account : allAccounts) {
            if (account.getUser().equals(user)) {
                userAccounts.add(account);
            }
        }
        return userAccounts;
    }

    public String validateBankAccount(BankAccount account) {
        if (bankRepository.findByIbanNumber(account.getIbanNumber()) != null) {
            return "Iban already exists!";
        } else {

            if (account.getIbanNumber().length() == 24) {
                User user = getCurrentUser();
                if (user != null) {
                    return "OK";
                } else {
                    return "User not found!";
                }
            } else {
                return "IBAN must be exactly 24 characters!";
            }
        }
    }

    public User getCurrentUser() {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            user = userRepository.findByUsername(currentUserName).orElse(null);
        }
        return user;
    }

}