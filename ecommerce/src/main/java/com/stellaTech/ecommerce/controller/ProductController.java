package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.service.ProductService;
import com.stellaTech.ecommerce.dto.product.ProductInsertDto;
import com.stellaTech.ecommerce.dto.product.ProductPatchDto;
import com.stellaTech.ecommerce.dto.product.ProductUpdateDto;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{idProduct}")
    public Product getProduct(@NonNull @PathVariable Long idProduct) {
        return productService.getProductById(idProduct);
    }

    @PutMapping("/products/{idProduct}")
    public ResponseEntity<Product> updateProduct(
            @NonNull @PathVariable Long idProduct,
            @NonNull @RequestBody ProductUpdateDto updatedProduct
    ) {
        Product savedProduct = productService.updateProduct(idProduct, updatedProduct);
        return ResponseEntity.ok(savedProduct);
    }

    @PatchMapping("/products/{idProduct}")
    public ResponseEntity<Product> partialUpdateUser(
            @NonNull @PathVariable Long idProduct,
            @NonNull @RequestBody ProductPatchDto updatedFields
    ) {
        Product savedProduct = productService.patchProduct(idProduct, updatedFields);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/products/{idProduct}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long idProduct) {
        productService.logicallyDeleteProduct(idProduct);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products")
    public Product createProduct(@NonNull @RequestBody ProductInsertDto newProduct) {
        return productService.createProduct(newProduct);
    }
}
