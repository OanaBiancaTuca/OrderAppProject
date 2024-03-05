package com.codejava.orderapp.repositories;

import com.codejava.orderapp.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
    public BankAccount findByIbanNumber(String ibanNumber);
}
