package com.codejava.orderapp.entities;

import com.codejava.orderapp.entities.order.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    @Column(unique = true)
    private String cardNumber;
    private Integer cvv;
    @Column(name = "expiry_month")
    private Integer expiryMonth;
    @Column(name = "expiry_year")
    private Integer expiryYear;
    private String nameOnCard;
    private String accountHolderName;
    @Column(unique = true)
    @Size(min = 24, max = 24, message = "Account number must be exactly 24 characters")
    private String ibanNumber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "bankAccount")
    private List<Order> ordersHistory;

}
