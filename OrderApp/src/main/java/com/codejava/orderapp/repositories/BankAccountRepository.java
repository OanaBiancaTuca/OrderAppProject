package com.codejava.orderapp.repositories;

import com.codejava.orderapp.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findByIbanNumber(String ibanNumber);
}
