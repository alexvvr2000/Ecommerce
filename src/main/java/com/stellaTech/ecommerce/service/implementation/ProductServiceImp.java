package com.stellaTech.ecommerce.service.implementation;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.repository.ProductRepository;
import com.stellaTech.ecommerce.repository.specification.ProductSpecs;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import com.stellaTech.ecommerce.service.generics.ProductService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
public class ProductServiceImp implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    @Qualifier("persistPropertyMapper")
    private ModelMapper persistPropertyManager;

    @Autowired
    @Qualifier("patchPropertyMapper")
    private ModelMapper patchPropertyMapper;

    @Transactional
    public void logicallyDeleteById(Long id) throws ResourceNotFoundException {
        Product product = this.productRepository.getProductById(id);
        product.setDeleted(true);
    }

    @Transactional
    public ProductDto updateProduct(Long productId, @Validated(NullCheckGroup.OnUpdate.class) ProductDto dto) throws ResourceNotFoundException {
        Product persistedProduct = this.productRepository.getProductById(productId);
        persistPropertyManager.map(dto, persistedProduct);
        productRepository.save(persistedProduct);
        return persistPropertyManager.map(persistedProduct, ProductDto.class);
    }

    @Transactional
    public ProductDto patchProduct(Long id, @Valid ProductDto dto) throws ResourceNotFoundException {
        Product persistedProduct = this.productRepository.getProductById(id);
        patchPropertyMapper.map(dto, persistedProduct);
        productRepository.save(persistedProduct);
        return persistPropertyManager.map(persistedProduct, ProductDto.class);
    }

    @Transactional
    public ProductDto createProduct(@Validated(NullCheckGroup.OnInsert.class) ProductDto dto) {
        Product persistedProduct = persistPropertyManager.map(dto, Product.class);
        productRepository.save(persistedProduct);
        return persistPropertyManager.map(persistedProduct, ProductDto.class);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsPaginated(@NonNull Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(ProductSpecs.hasNotBeenDeleted(), pageable);
        return productPage.map(product -> persistPropertyManager.map(product, ProductDto.class));
    }

    @Transactional(readOnly = true)
    public ProductDto getProductDtoById(Long id) throws ResourceNotFoundException {
        return persistPropertyManager.map(this.productRepository.getProductById(id), ProductDto.class);
    }
}
