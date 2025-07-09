package com.stellaTech.ecommerce.service.dto.platformUser;

import com.stellaTech.ecommerce.model.PlatformUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class PlatformUserMapper {
    @Mapping(target = "deleted", ignore = true)
    public abstract PlatformUser toEntity(PlatformUserInsertDto platformUserInsertDto);

    @Mapping(target = "deleted", ignore = true)
    public abstract PlatformUser updatePlatformUserFromDto(@MappingTarget PlatformUser entity, PlatformUserPatchDto dto);

}
