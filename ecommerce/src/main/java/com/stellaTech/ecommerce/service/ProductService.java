package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.service.repository.ProductRepository;
import com.stellaTech.ecommerce.service.specification.ProductSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Long logicalDeleteProduct(Long id) throws Exception {
        Product product = getProductById(id);
        if (product.isDeleted()) {
            throw new IllegalStateException("Product with id " + id + " was already deleted");
        }
        product.setDeleted(true);
        return id;
    }

    @Transactional
    public Product updateEntireProduct(Long productId, Product updatedProduct) {
        Product existingProduct = getProductById(productId);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setAverageRating(updatedProduct.getAverageRating());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setMdFormatDescription(updatedProduct.getMdFormatDescription());
        existingProduct.setMainImageUrl(updatedProduct.getMainImageUrl());

        return productRepository.save(existingProduct);
    }

    @Transactional
    public Product updateProductPartially(Long id, Map<String, Object> updatedFields) {
        Product product = getProductById(id);
        updatedFields.forEach((key, value) -> {
            switch (key) {
                case "name":
                    product.setName((String) value);
                    break;
                case "averageRating":
                    product.setAverageRating(new BigDecimal(value.toString()));
                    break;
                case "price":
                    product.setPrice(new BigDecimal(value.toString()));
                    break;
                case "mdFormatDescription":
                    product.setMdFormatDescription((String) value);
                    break;
                case "mainImageUrl":
                    product.setMainImageUrl((String) value);
                    break;
            }
        });
        return productRepository.save(product);
    }

    @Transactional
    public Product createProduct(Product newProduct) {
        return productRepository.save(newProduct);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findAll(ProductSpecs.isNotDeleted());
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findOne(
                ProductSpecs.activeProductById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }

}
