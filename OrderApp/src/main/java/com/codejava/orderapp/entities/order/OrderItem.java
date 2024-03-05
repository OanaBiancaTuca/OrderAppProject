package com.codejava.orderapp.entities.order;

import com.codejava.orderapp.entities.Product;
import com.codejava.orderapp.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Table(name = "order_items")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItem_id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

     @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public String writeOrderItemDescription() {
        return "OrderItem{" +
                "orderItem_id=" + orderItem_id +
                ", product=" + product.getName() +
                ", quantity=" + quantity +
                ", customer = "+user.getId()+
                ", order=" + order +
                '}';
    }


}
