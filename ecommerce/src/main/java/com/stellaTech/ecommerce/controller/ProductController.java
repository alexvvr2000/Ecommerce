package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllActiveProducts();
    }

    @GetMapping("/products/{idProduct}")
    public Product getProduct(@PathVariable Long idProduct) {
        return productService.getProductById(idProduct);
    }

    @PutMapping("/products/{idProduct}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long idProduct,
            @RequestBody Product updatedProduct
    ) {
        Product savedProduct = productService.updateEntireProduct(idProduct, updatedProduct);
        return ResponseEntity.ok(savedProduct);
    }

    @PatchMapping("/users/{idProduct}")
    public ResponseEntity<Product> partialUpdateUser(
            @PathVariable Long idProduct,
            @RequestBody Map<String, Object> updatedFields
    ) {
        Product savedProduct = productService.updateProductPartially(idProduct, updatedFields);
        return ResponseEntity.ok(savedProduct);
    }
}
