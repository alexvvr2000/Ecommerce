package com.stellaTech.ecommerce.service.implementation;

import com.stellaTech.ecommerce.exception.instance.InvalidPasswordField;
import com.stellaTech.ecommerce.exception.instance.RepeatedUserPassword;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUserPassword;
import com.stellaTech.ecommerce.repository.PlatformUserPasswordRepository;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.generics.PlatformUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
public class PlatformUserServiceImp implements PlatformUserService {
    @Autowired
    private PlatformUserRepository userRepository;

    @Autowired
    private PlatformUserPasswordRepository userPasswordRepository;

    @Autowired
    @Qualifier("persistPropertyMapper")
    private ModelMapper persistPropertyManager;

    @Autowired
    @Qualifier("patchPropertyMapper")
    private ModelMapper patchPropertyMapper;

    @Transactional
    public void logicallyDeleteById(Long id) throws ResourceNotFoundException {
        PlatformUser user = this.userRepository.getUserById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(
            @Valid @NotNull PasswordChangeDto dto, @NotNull Long platformUserId
    ) throws ResourceNotFoundException, RepeatedUserPassword, InvalidPasswordField {
        PlatformUserPassword password = this.userPasswordRepository.getPasswordByUserId(platformUserId);
        if (!password.getPassword().equals(dto.getOldPassword())) {
            throw new InvalidPasswordField("Incorrect old password");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new InvalidPasswordField("New passwords do not match");
        }
        if (dto.getNewPassword().equals(password.getPassword())) {
            throw new InvalidPasswordField("New password must be different from current password");
        }
        password.setPassword(dto.getNewPassword());
        userPasswordRepository.save(password);
    }

    @Transactional
    public PlatformUserDto updatePlatformUser(
            Long idUser, @Validated(NullCheckGroup.OnUpdate.class) PlatformUserDto dto
    ) throws ResourceNotFoundException {
        PlatformUser persistedUser = this.userRepository.getUserById(idUser);
        persistPropertyManager.map(dto, persistedUser);
        userRepository.save(persistedUser);
        return persistPropertyManager.map(persistedUser, PlatformUserDto.class);
    }

    @Transactional
    public PlatformUserDto patchPlatformUser(
            Long idUser, @Valid PlatformUserDto dto
    ) throws ResourceNotFoundException {
        PlatformUser persistedUser = this.userRepository.getUserById(idUser);
        patchPropertyMapper.map(dto, persistedUser);
        userRepository.save(persistedUser);
        return persistPropertyManager.map(persistedUser, PlatformUserDto.class);
    }

    @Transactional
    public PlatformUserDto createUser(@Validated(NullCheckGroup.OnInsert.class) PlatformUserDto dto) {
        PlatformUser persistedUser = persistPropertyManager.map(dto, PlatformUser.class);
        userRepository.save(persistedUser);
        return persistPropertyManager.map(persistedUser, PlatformUserDto.class);
    }

    @Transactional(readOnly = true)
    public Page<PlatformUserDto> getAllPlatformUsers(@NonNull Pageable pageable) {
        Page<PlatformUser> users = userRepository.findAll(PlatformUserSpecs.hasNotBeenDeleted(), pageable);
        return users.map(
                currentPlatformUser -> persistPropertyManager.map(currentPlatformUser, PlatformUserDto.class)
        );
    }

    @Transactional(readOnly = true)
    public PlatformUserDto getUserDtoById(Long id) throws ResourceNotFoundException {
        return persistPropertyManager.map(this.userRepository.getUserById(id), PlatformUserDto.class);
    }
}
