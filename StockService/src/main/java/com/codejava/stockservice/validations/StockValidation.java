package com.codejava.stockservice.validations;

import com.codejava.orderapp.entities.Product;
import com.codejava.orderapp.entities.order.OrderItem;

import java.util.List;

public class StockValidation {

    public static boolean validateStock(List<OrderItem> items) {
        Boolean isValidOrder = true;
        //Validation for stock for every product ordered.
        for (OrderItem orderItem : items) {
            Product product = orderItem.getProduct();
            if (product.getQuantity() < orderItem.getQuantity())
                isValidOrder = false;
        }
        return isValidOrder;
    }
}
