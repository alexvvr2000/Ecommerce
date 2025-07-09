package com.stellaTech.ecommerce.service.dto.platformUser;

import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.service.PlatformUserService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PlatformUserMapper {
    @Autowired
    protected PlatformUserService platformUserService;

    @Mapping(target = "deleted", ignore = true)
    abstract PlatformUser toEntity(PlatformUserInsertDto platformUserInsertDto);

    @Mapping(target = "deleted", ignore = true)
    abstract void updatePlatformUserFromDto(@MappingTarget PlatformUser entity, PlatformUserInsertDto dto);
}
