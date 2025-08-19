package com.stellaTech.ecommerce.service.unitTest.implementation;

import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.repository.ProductRepository;
import com.stellaTech.ecommerce.repository.specification.ProductSpecs;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.implementation.ProductServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {
    protected final DataGenerationService dataGenerationService = new DataGenerationService();
    protected final DataGenerationService.NumberRange idCreationRange =
            DataGenerationService.NumberRange.builder()
                    .minAmount(1)
                    .maxAmount(100)
                    .build();

    @Mock
    protected ProductRepository productRepository;

    @Mock
    @Qualifier("persistPropertyMapper")
    protected ModelMapper persistPropertyMapper;

    @Mock
    @Qualifier("patchPropertyMapper")
    protected ModelMapper patchPropertyMapper;

    @InjectMocks
    protected ProductServiceImp productService;

    @Test
    void createProductWithValidData() throws NoSuchFieldException, IllegalAccessException {
        Long testProductId = 1L;
        ProductDto validProductDto = dataGenerationService.createValidProductDto(testProductId);
        Product productModel = dataGenerationService.createProductModel(validProductDto);

        when(productRepository.save(any(Product.class))).thenReturn(productModel);
        when(persistPropertyMapper.map(validProductDto, Product.class)).thenReturn(productModel);
        when(persistPropertyMapper.map(productModel, ProductDto.class)).thenReturn(validProductDto);

        ProductDto result = productService.createProduct(validProductDto);

        assertNotNull(result);
        assertEquals(testProductId, result.getId());
        assertEquals(validProductDto.getName(), result.getName());
        assertEquals(validProductDto.getPrice(), result.getPrice());
        assertEquals(validProductDto.getMdFormatDescription(), result.getMdFormatDescription());
    }

    @Test
    void getProductDtoByIdWithExistingProduct() throws NoSuchFieldException, IllegalAccessException, ResourceNotFoundException {
        Long testProductId = 1L;
        ProductDto expectedProductDto = dataGenerationService.createValidProductDto(testProductId);
        Product productModel = dataGenerationService.createProductModel(expectedProductDto);

        when(productRepository.getProductById(testProductId)).thenReturn(productModel);
        when(persistPropertyMapper.map(productModel, ProductDto.class)).thenReturn(expectedProductDto);

        ProductDto result = productService.getProductDtoById(testProductId);

        assertNotNull(result);
        assertEquals(testProductId, result.getId());
        assertEquals(expectedProductDto.getName(), result.getName());
        assertEquals(expectedProductDto.getPrice(), result.getPrice());
    }

    @Test
    void getProductDtoByIdWithNonExistingProduct() {
        Long nonExistingProductId = 999L;

        when(productRepository.getProductById(nonExistingProductId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductDtoById(nonExistingProductId);
        });
    }

    @Test
    void getAllProductsPaginated() {
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<ProductDto> productDtoList = dataGenerationService.createValidListProductDto(5, idCreationRange);
        List<Product> productList = productDtoList.stream()
                .map(dto -> {
                    try {
                        return dataGenerationService.createProductModel(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(ProductSpecs.hasNotBeenDeleted(), pageable)).thenReturn(productPage);
        when(persistPropertyMapper.map(any(Product.class), eq(ProductDto.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    return productDtoList.stream()
                            .filter(dto -> dto.getId().equals(product.getId()))
                            .findFirst()
                            .orElseThrow();
                });

        Page<ProductDto> result = productService.getAllProductsPaginated(pageable);

        assertNotNull(result);
        assertEquals(productList.size(), result.getContent().size());
        assertEquals(pageable.getPageSize(), result.getSize());
        assertEquals(pageNumber, result.getNumber());

        result.getContent().forEach(productDto -> {
            assertTrue(productDtoList.stream()
                    .anyMatch(expected -> expected.getId().equals(productDto.getId())));
        });
    }

    @Test
    void getAllProductsPaginatedEmptyPage() {
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Product> emptyPage = Page.empty(pageable);
        when(productRepository.findAll(ProductSpecs.hasNotBeenDeleted(), pageable)).thenReturn(emptyPage);

        Page<ProductDto> result = productService.getAllProductsPaginated(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getContent().size());
    }
}
