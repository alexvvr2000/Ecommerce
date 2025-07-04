package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
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

    @PatchMapping("/products/{idProduct}")
    public ResponseEntity<Product> partialUpdateUser(
            @PathVariable Long idProduct,
            @RequestBody Map<String, Object> updatedFields
    ) {
        Product savedProduct = productService.updateProductPartially(idProduct, updatedFields);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/products/{idProduct}")
    public ResponseEntity<?> logicalDeletePlatformUser(@PathVariable Long idProduct) {
        try {
            productService.logicalDeleteProduct(idProduct);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Error("Internal server error"));
        }
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product newProduct) {
        return productService.createProduct(newProduct);
    }
}
