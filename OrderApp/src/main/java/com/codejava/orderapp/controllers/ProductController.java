package com.codejava.orderapp.controllers;

import com.codejava.orderapp.entities.Product;
import com.codejava.orderapp.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products")
@RestController
public class ProductController {


    ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> postProducts(@RequestBody Product product) {
        String responseValidateProdact = productService.validateProduct(product);
        if (responseValidateProdact.equals("OK")) {
            productService.addProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(product.toString());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseValidateProdact);
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        if (productService.searchProductById(productId) != null) {
            productService.deleteProduct(productId);
            return ResponseEntity.status(HttpStatus.OK).body("Product is successfully deleted ");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product is not found");
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId, @Valid @RequestBody Product updatedProduct) {
        String updateMessage = productService.updateProduct(productId, updatedProduct);
        if (updateMessage.equals("OK")) {
            return ResponseEntity.status(HttpStatus.OK).body(updateMessage);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(updateMessage);
        }
    }


}
