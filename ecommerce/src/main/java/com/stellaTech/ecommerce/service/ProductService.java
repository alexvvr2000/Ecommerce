package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.ProductManagement.Product;
import com.stellaTech.ecommerce.repository.ProductRepository;
import com.stellaTech.ecommerce.repository.specification.ProductSpecs;
import com.stellaTech.ecommerce.service.dataDto.ProductDto;
import com.stellaTech.ecommerce.service.serviceDto.IdDtoResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void logicallyDeleteProduct(Long id) throws ResourceNotFoundException {
        Product product = getProductById(id);
        product.setDeleted(true);
    }

    @Transactional
    public ProductDto updateProduct(Long productId, @Valid ProductDto dto) throws ResourceNotFoundException {
        Product persistedProduct = getProductById(productId);
        modelMapper.getConfiguration()
                .setSkipNullEnabled(false)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(dto, persistedProduct);
        productRepository.save(persistedProduct);
        return modelMapper.map(persistedProduct, ProductDto.class);
    }

    @Transactional
    public ProductDto patchProduct(Long id, @Valid ProductDto dto) throws ResourceNotFoundException {
        Product persistedProduct = getProductById(id);
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(dto, persistedProduct);
        productRepository.save(persistedProduct);
        return modelMapper.map(persistedProduct, ProductDto.class);
    }

    @Transactional
    public IdDtoResponse<ProductDto> createProduct(@Valid ProductDto dto) {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(false)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        Product persistedProduct = modelMapper.map(dto, Product.class);
        productRepository.save(persistedProduct);
        return new IdDtoResponse<>(persistedProduct.getId(), dto);
    }

    @Transactional(readOnly = true)
    public List<IdDtoResponse<ProductDto>> getAllProducts() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(false)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return productRepository.findAll(ProductSpecs.hasNotBeenDeleted()).stream().map(
                currentProduct -> {
                    ProductDto currentProductDto = modelMapper.map(currentProduct, ProductDto.class);
                    ;
                    Long currentId = currentProduct.getId();
                    return new IdDtoResponse<>(currentId, currentProductDto);
                }
        ).toList();
    }

    @Transactional(readOnly = true)
    protected Product getProductById(Long id) throws ResourceNotFoundException {
        return productRepository.findOne(
                ProductSpecs.activeProductById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }

    @Transactional(readOnly = true)
    public ProductDto getProductDtoById(Long id) throws ResourceNotFoundException {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(false)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(getProductById(id), ProductDto.class);
    }
}
