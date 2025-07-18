package com.stellaTech.ecommerce.restController;

import com.stellaTech.ecommerce.service.ProductService;
import com.stellaTech.ecommerce.service.dataDto.ProductDto;
import com.stellaTech.ecommerce.service.serviceDto.IdDtoResponse;
import jakarta.validation.Valid;
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
    public List<IdDtoResponse<ProductDto>> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{idProduct}")
    public ResponseEntity<ProductDto> getProduct(@NonNull @PathVariable Long idProduct) {
        ProductDto newProduct = productService.getProductDtoById(idProduct);
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/products/{idProduct}")
    public ResponseEntity<ProductDto> updateProduct(
            @NonNull @PathVariable Long idProduct,
            @Valid @RequestBody ProductDto productUpdateDto
    ) {
        ProductDto savedProduct = productService.updateProduct(idProduct, productUpdateDto);
        return ResponseEntity.ok(savedProduct);
    }

    @PatchMapping("/products/{idProduct}")
    public ResponseEntity<ProductDto> partialUpdateUser(
            @NonNull @PathVariable Long idProduct,
            @Valid @RequestBody ProductDto productPatchDto
    ) {
        ProductDto savedProduct = productService.patchProduct(idProduct, productPatchDto);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/products/{idProduct}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long idProduct) {
        productService.logicallyDeleteProduct(idProduct);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products")
    public ResponseEntity<IdDtoResponse<ProductDto>> createProduct(@Valid @RequestBody ProductDto productInsertDto) {
        IdDtoResponse<ProductDto> newProduct = productService.createProduct(productInsertDto);
        return ResponseEntity.ok(newProduct);
    }
}
