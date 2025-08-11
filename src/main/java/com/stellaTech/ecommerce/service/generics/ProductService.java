package com.stellaTech.ecommerce.service.generics;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService extends LogicallyDeletableEntityService {
    ProductDto updateProduct(Long productId, ProductDto dto) throws ResourceNotFoundException;

    ProductDto patchProduct(Long id, ProductDto dto) throws ResourceNotFoundException;

    ProductDto createProduct(ProductDto dto);

    Page<ProductDto> getAllProductsPaginated(Pageable pageable);

    ProductDto getProductDtoById(Long id) throws ResourceNotFoundException;
}
