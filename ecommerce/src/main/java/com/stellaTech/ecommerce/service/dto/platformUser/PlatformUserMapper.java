package com.stellaTech.ecommerce.service.dto.platformUser;

import com.stellaTech.ecommerce.model.PlatformUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class PlatformUserMapper {
    @Mapping(target = "deleted", ignore = true)
    public abstract PlatformUser toEntity(PlatformUserInsertDto platformUserInsertDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "deleted", ignore = true)
    public abstract PlatformUser updatePlatformUserFromDto(@MappingTarget PlatformUser entity, PlatformUserPatchDto dto);
}
