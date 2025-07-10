package com.stellaTech.ecommerce.service.dto.product;

import com.stellaTech.ecommerce.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    @Mapping(target = "deleted", ignore = true)
    public abstract Product createProductInstance(ProductInsertDto productInsertDto);

    @Mapping(target = "deleted", ignore = true)
    public abstract Product updateProduct(@MappingTarget Product product, ProductInsertDto dto);

    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Product patchProduct(@MappingTarget Product product, ProductPatchDto dto);
}
