package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.dto.product.ProductInsertDto;
import com.stellaTech.ecommerce.dto.product.ProductMapper;
import com.stellaTech.ecommerce.dto.product.ProductPatchDto;
import com.stellaTech.ecommerce.dto.product.ProductUpdateDto;
import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.repository.ProductRepository;
import com.stellaTech.ecommerce.repository.specification.ProductSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    private ProductMapper productMapper;

    @Transactional
    public void logicallyDeleteProduct(Long id) throws ResourceNotFoundException {
        Product product = getProductById(id);
        product.setDeleted(true);
    }

    @Transactional
    public Product updateProduct(Long productId, ProductUpdateDto updatedData) throws ResourceNotFoundException {
        Product persistedProduct = getProductById(productId);
        Product updatedProduct = productMapper.updateProductFromDto(persistedProduct, updatedData);
        return productRepository.save(updatedProduct);
    }

    @Transactional
    public Product patchProduct(Long id, ProductPatchDto updatedFields) throws ResourceNotFoundException, InvalidInputException {
        Product persistedProduct = getProductById(id);
        Product updatedProduct = productMapper.patchProductFromDto(persistedProduct, updatedFields);
        return productRepository.save(updatedProduct);
    }

    @Transactional
    public Product createProduct(ProductInsertDto newProduct) {
        Product persistedProduct = productMapper.createProductInstance(newProduct);
        return productRepository.save(persistedProduct);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll(ProductSpecs.hasNotBeenDeleted());
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) throws ResourceNotFoundException {
        return productRepository.findOne(
                ProductSpecs.activeProductById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }

}
