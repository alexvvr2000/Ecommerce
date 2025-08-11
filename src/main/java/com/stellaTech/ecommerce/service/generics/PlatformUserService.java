package com.stellaTech.ecommerce.service.generics;

import com.stellaTech.ecommerce.exception.instance.InvalidPasswordField;
import com.stellaTech.ecommerce.exception.instance.RepeatedUserPassword;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformUserService extends LogicallyDeletableEntityService {
    void changePassword(
            PasswordChangeDto dto, Long platformUserId
    ) throws ResourceNotFoundException, RepeatedUserPassword, InvalidPasswordField;

    PlatformUserDto updatePlatformUser(
            Long idUser, PlatformUserDto dto
    ) throws ResourceNotFoundException;

    PlatformUserDto patchPlatformUser(
            Long idUser, PlatformUserDto dto
    ) throws ResourceNotFoundException;

    PlatformUserDto createUser(PlatformUserDto dto);

    Page<PlatformUserDto> getAllPlatformUsers(Pageable pageable);

    PlatformUserDto getUserDtoById(Long id) throws ResourceNotFoundException;
}
