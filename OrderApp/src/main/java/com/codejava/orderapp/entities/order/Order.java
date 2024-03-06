package com.codejava.orderapp.entities.order;

import com.codejava.orderapp.entities.BankAccount;
import com.codejava.orderapp.entities.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
@EnableAutoConfiguration
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;
    private double totalAmount;
    private OrderStatus status;


}
