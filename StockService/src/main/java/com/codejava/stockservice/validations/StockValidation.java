package com.codejava.stockservice.validations;

import com.codejava.orderapp.entities.Product;
import com.codejava.orderapp.entities.order.OrderItem;

import java.util.List;

public class StockValidation {
    private StockValidation() {
    }

    public static String validateStock(List<OrderItem> items) {
        //Validation for stock for every product ordered.
        for (OrderItem orderItem : items) {
            Product product = orderItem.getProduct();
            if (product.getQuantity() < orderItem.getQuantity())
                return "REJECTED";
        }
        return "ACCEPTED";
    }
}
