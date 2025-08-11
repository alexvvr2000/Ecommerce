package com.stellaTech.ecommerce.service.generics;

import com.stellaTech.ecommerce.exception.instance.InvalidPasswordField;
import com.stellaTech.ecommerce.exception.instance.RepeatedUserPassword;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

public interface PlatformUserService extends LogicallyDeletableEntityService {
    void changePassword(
            @Valid @NotNull PasswordChangeDto dto, @NotNull Long platformUserId
    ) throws ResourceNotFoundException, RepeatedUserPassword, InvalidPasswordField;

    PlatformUserDto updatePlatformUser(
            Long idUser, @Validated(NullCheckGroup.OnUpdate.class) PlatformUserDto dto
    ) throws ResourceNotFoundException;

    PlatformUserDto patchPlatformUser(
            Long idUser, @Valid PlatformUserDto dto
    ) throws ResourceNotFoundException;

    PlatformUserDto createUser(@Validated(NullCheckGroup.OnInsert.class) PlatformUserDto dto);

    Page<PlatformUserDto> getAllPlatformUsers(@NonNull Pageable pageable);

    PlatformUserDto getUserDtoById(Long id) throws ResourceNotFoundException;
}
