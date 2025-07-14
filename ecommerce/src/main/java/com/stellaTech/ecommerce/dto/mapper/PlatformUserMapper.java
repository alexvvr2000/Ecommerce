package com.stellaTech.ecommerce.dto.mapper;

import com.stellaTech.ecommerce.dto.platformUser.PasswordChangeDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserInsertDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserPatchDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserUpdateDto;
import com.stellaTech.ecommerce.model.PlatformUser;
import jakarta.validation.Valid;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PlatformUserMapper {
    @Mapping(target = "deleted", ignore = true)
    PlatformUser createPlatformUserEntity(@Valid PlatformUserInsertDto platformUserInsertDto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    PlatformUser updatePlatformUserFromDto(@MappingTarget PlatformUser entity,@Valid PlatformUserUpdateDto dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PlatformUser patchPlatformUserFromDto(@MappingTarget PlatformUser entity,@Valid PlatformUserPatchDto dto);

    @Mapping(target = "rfc", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "curp", ignore = true)
    @Mapping(target = "password", source = "newPassword")
    PlatformUser patchPlatformUserPassword(@MappingTarget PlatformUser entity,@Valid PasswordChangeDto dto);
}
