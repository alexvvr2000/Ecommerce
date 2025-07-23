package com.stellaTech.ecommerce.restController;

import com.stellaTech.ecommerce.service.ProductService;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.ValidationGroup;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/products")
    public Page<ProductDto> getAllProducts(
            @SortDefault("id")
            @NonNull final Pageable pageable) {
        log.info("Obtained page of product");
        return productService.getAllProductsPaginated(pageable);
    }

    @GetMapping("/products/{idProduct}")
    public ResponseEntity<ProductDto> getProduct(@NonNull @PathVariable Long idProduct) {
        ProductDto newProduct = productService.getProductDtoById(idProduct);
        log.info("Read product with id {}", idProduct);
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/products/{idProduct}")
    public ResponseEntity<ProductDto> updateProduct(
            @NonNull @PathVariable Long idProduct,
            @Validated(ValidationGroup.OnUpdate.class) @RequestBody ProductDto productUpdateDto
    ) {
        ProductDto savedProduct = productService.updateProduct(idProduct, productUpdateDto);
        log.info("Updated product with id {}", idProduct);
        return ResponseEntity.ok(savedProduct);
    }

    @PatchMapping("/products/{idProduct}")
    public ResponseEntity<ProductDto> partialUpdateUser(
            @NonNull @PathVariable Long idProduct,
            @Valid @RequestBody ProductDto productPatchDto
    ) {
        ProductDto savedProduct = productService.patchProduct(idProduct, productPatchDto);
        log.info("Patched product with id {}", idProduct);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/products/{idProduct}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long idProduct) {
        productService.logicallyDeleteProduct(idProduct);
        log.info("Deleted product with id {}", idProduct);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Validated(ValidationGroup.OnInsert.class) @RequestBody ProductDto productInsertDto) {
        ProductDto newProduct = productService.createProduct(productInsertDto);
        log.info("Created product with id {}", newProduct.getId());
        return ResponseEntity.ok(newProduct);
    }
}
