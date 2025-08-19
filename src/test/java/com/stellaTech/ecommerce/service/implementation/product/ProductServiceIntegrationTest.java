package com.stellaTech.ecommerce.service.implementation.product;

import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.generics.ProductService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductServiceIntegrationTest {
    protected final Long testProductId = 1L;
    protected final Long nonExistentProductId = 999L;
    protected final int testProductAmount = 5;

    @Autowired
    protected DataGenerationService dataGenerationService;

    @Autowired
    protected ProductService productService;

    @Test
    void createProductWithValidData() {
        ProductDto productDto = dataGenerationService.createValidProductDto(testProductId);

        ProductDto createdProduct = productService.createProduct(productDto);

        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getId());
        assertEquals(productDto.getName(), createdProduct.getName());
        assertEquals(productDto.getMdFormatDescription(), createdProduct.getMdFormatDescription());
        assertEquals(productDto.getPrice(), createdProduct.getPrice());
    }

    @Test
    void getAllProductsPaginatedReturnsCorrectResults() {
        List<ProductDto> createdProducts = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ProductDto productDto = dataGenerationService.createValidProductDto((long) i);
            createdProducts.add(productService.createProduct(productDto));
        }

        Page<ProductDto> firstPage = productService.getAllProductsPaginated(
                PageRequest.of(0, 2, Sort.by("id"))
        );
        Page<ProductDto> secondPage = productService.getAllProductsPaginated(
                PageRequest.of(1, 2, Sort.by("id"))
        );

        assertEquals(2, firstPage.getContent().size());
        assertFalse(secondPage.getContent().isEmpty());
        assertTrue(firstPage.getTotalElements() >= 3);

        assertTrue(firstPage.getContent().stream().anyMatch(
                        product -> product.getId().equals(createdProducts.getFirst().getId())
                )
        );
    }

    @Test
    void getProductDtoByIdWhenProductExists() {
        ProductDto productDto = dataGenerationService.createValidProductDto(testProductId);
        ProductDto createdProduct = productService.createProduct(productDto);

        ProductDto retrievedProduct = productService.getProductDtoById(createdProduct.getId());

        assertNotNull(retrievedProduct);
        assertEquals(createdProduct.getId(), retrievedProduct.getId());
        assertEquals(createdProduct.getName(), retrievedProduct.getName());
        assertEquals(createdProduct.getPrice(), retrievedProduct.getPrice());
    }

    @Test
    void getProductDtoByIdWhenProductDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () ->
                productService.getProductDtoById(nonExistentProductId)
        );
    }

    @Test
    void logicallyDeleteByIdWhenProductExists() {
        ProductDto productDto = dataGenerationService.createValidProductDto(testProductId);
        ProductDto createdProduct = productService.createProduct(productDto);
        Long productId = createdProduct.getId();

        assertNotNull(productService.getProductDtoById(productId));

        productService.logicallyDeleteById(productId);

        assertThrows(ResourceNotFoundException.class, () ->
                productService.getProductDtoById(productId)
        );
    }

    @Test
    void logicallyDeleteNonExistentProduct() {
        assertThrows(ResourceNotFoundException.class, () ->
                productService.logicallyDeleteById(nonExistentProductId)
        );
    }

    @Test
    void getAllProductsPaginatedExcludesLogicallyDeletedProducts() {
        ProductDto activeProduct = productService.createProduct(
                dataGenerationService.createValidProductDto(1L)
        );
        ProductDto productToDelete = productService.createProduct(
                dataGenerationService.createValidProductDto(2L)
        );

        productService.logicallyDeleteById(productToDelete.getId());

        Page<ProductDto> allProducts = productService.getAllProductsPaginated(Pageable.unpaged());

        assertTrue(allProducts.getContent().stream().anyMatch(
                        product -> product.getId().equals(activeProduct.getId())
                )
        );
        assertTrue(allProducts.getContent().stream().noneMatch(
                        product -> product.getId().equals(productToDelete.getId())
                )
        );
    }

    @Test
    void createValidListOfProducts() {
        DataGenerationService.NumberRange productIdRange = DataGenerationService.NumberRange.builder()
                .minAmount(10)
                .maxAmount(20)
                .build();

        List<ProductDto> productDtoList = dataGenerationService.createValidListProductDto(
                testProductAmount, productIdRange
        );

        List<ProductDto> createdProducts = productDtoList.stream()
                .map(productService::createProduct)
                .toList();

        assertEquals(testProductAmount, createdProducts.size());
        createdProducts.forEach(product -> {
            assertNotNull(product.getId());
            assertNotNull(product.getName());
            assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0);
        });
    }

    @Test
    void productPricePrecisionIsMaintained() {

        ProductDto productDto = dataGenerationService.createValidProductDto(testProductId);
        productDto.setPrice(new BigDecimal("19.99"));

        ProductDto createdProduct = productService.createProduct(productDto);
        ProductDto retrievedProduct = productService.getProductDtoById(createdProduct.getId());

        assertEquals(
                0,
                new BigDecimal("19.99").compareTo(retrievedProduct.getPrice())
        );
    }

    @Test
    void emptyProductListWhenNoProductsExist() {
        Page<ProductDto> productsPage = productService.getAllProductsPaginated(Pageable.unpaged());

        assertNotNull(productsPage);
        assertTrue(productsPage.isEmpty());
    }

    @Test
    void productPaginationWithSorting() {
        ProductDto productA = dataGenerationService.createValidProductDto(1L);
        productA.setName("ZProduct");
        productService.createProduct(productA);

        ProductDto productB = dataGenerationService.createValidProductDto(2L);
        productB.setName("AProduct");
        productService.createProduct(productB);

        Page<ProductDto> sortedProducts = productService.getAllProductsPaginated(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
        );

        List<ProductDto> content = sortedProducts.getContent();
        if (content.size() >= 2) {
            assertTrue(content.get(0).getName().compareTo(content.get(1).getName()) <= 0);
        }
    }
}
