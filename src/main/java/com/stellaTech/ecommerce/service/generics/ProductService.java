package com.stellaTech.ecommerce.service.generics;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

public interface ProductService extends LogicallyDeletableEntityService {
    ProductDto updateProduct(Long productId, @Validated(NullCheckGroup.OnUpdate.class) ProductDto dto) throws ResourceNotFoundException;

    ProductDto patchProduct(Long id, @Valid ProductDto dto) throws ResourceNotFoundException;

    ProductDto createProduct(@Validated(NullCheckGroup.OnInsert.class) ProductDto dto);

    Page<ProductDto> getAllProductsPaginated(@NonNull Pageable pageable);

    ProductDto getProductDtoById(Long id) throws ResourceNotFoundException;
}
