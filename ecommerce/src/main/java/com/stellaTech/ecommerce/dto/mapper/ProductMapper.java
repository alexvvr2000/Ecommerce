package com.stellaTech.ecommerce.dto.mapper;

import com.stellaTech.ecommerce.dto.product.ProductInsertDto;
import com.stellaTech.ecommerce.dto.product.ProductPatchDto;
import com.stellaTech.ecommerce.dto.product.ProductUpdateDto;
import com.stellaTech.ecommerce.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    @Mapping(target = "deleted", ignore = true)
    public abstract Product createProductInstance(ProductInsertDto productInsertDto);

    @Mapping(target = "deleted", ignore = true)
    public abstract Product updateProductFromDto(@MappingTarget Product product, ProductUpdateDto dto);

    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Product patchProductFromDto(@MappingTarget Product product, ProductPatchDto dto);
}
