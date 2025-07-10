package com.stellaTech.ecommerce.service.dto.platformUser;

import com.stellaTech.ecommerce.model.PlatformUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class PlatformUserMapper {
    @Mapping(target = "deleted", ignore = true)
    public abstract PlatformUser createPlatformUserEntity(PlatformUserInsertDto platformUserInsertDto);

    @Mapping(target = "deleted", ignore = true)
    public abstract PlatformUser updatePlatformUserFromDto(@MappingTarget PlatformUser entity, PlatformUserUpdateDto dto);

    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract PlatformUser patchPlatformUserFromDto(@MappingTarget PlatformUser entity, PlatformUserPatchDto dto);
}
