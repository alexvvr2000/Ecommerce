package com.stellaTech.ecommerce.service.dto.product;

import com.stellaTech.ecommerce.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    @Mapping(target = "deleted", ignore = true)
    public abstract Product toEntity(ProductInsertDto productInsertDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "deleted", ignore = true)
    public abstract Product updateProductFromDto(@MappingTarget Product product, ProductPatchDto dto);
}
