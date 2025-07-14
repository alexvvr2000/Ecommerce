package com.stellaTech.ecommerce.dto.mapper;

import com.stellaTech.ecommerce.dto.product.ProductInsertDto;
import com.stellaTech.ecommerce.dto.product.ProductPatchDto;
import com.stellaTech.ecommerce.dto.product.ProductUpdateDto;
import com.stellaTech.ecommerce.model.ProductManagement.Product;
import jakarta.validation.Valid;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    @Mapping(target = "deleted", ignore = true)
    public abstract Product createProductInstance(@Valid ProductInsertDto productInsertDto);

    @Mapping(target = "deleted", ignore = true)
    public abstract Product updateProductFromDto(@MappingTarget Product product,@Valid ProductUpdateDto dto);

    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Product patchProductFromDto(@MappingTarget Product product,@Valid ProductPatchDto dto);
}
