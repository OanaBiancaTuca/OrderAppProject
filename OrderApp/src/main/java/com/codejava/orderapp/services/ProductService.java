package com.codejava.orderapp.services;

import com.codejava.orderapp.entities.Product;
import com.codejava.orderapp.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public String validateProduct(Product product) {
        if (!product.getName().isEmpty()) {
            if (product.getPrice() > 0) {
                if (product.getQuantity() > 0) {
                    return "OK";
                } else {
                    return "Quantity must be greater than 0";
                }
            } else {
                return "Price must be greater than 0";
            }

        } else {
            return "Product name cannot be empty!";
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public Product searchProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    public String updateProduct(Long productId, Product updatedProduct) {
        Product existingProduct = searchProductById(productId);
            // Update fields of existingProduct with fields from updatedProduct
        if(existingProduct!=null){
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            // Save the updated product
            productRepository.save(existingProduct);
            return "OK";
        } else {
            return "Product not found";
        }
    }
}


