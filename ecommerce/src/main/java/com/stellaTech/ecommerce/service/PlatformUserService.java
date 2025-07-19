package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.dto.ValidationGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class PlatformUserService {
    @Autowired
    private PlatformUserRepository userRepository;

    @Autowired
    @Qualifier("persistPropertyMapper")
    private ModelMapper persistPropertyManager;

    @Autowired
    @Qualifier("patchPropertyMapper")
    private ModelMapper patchPropertyMapper;

    @Transactional
    public void logicallyDeleteUser(Long id) throws ResourceNotFoundException {
        PlatformUser user = getUserById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(@Valid @NotNull PasswordChangeDto dto, @NotNull Long platformUserId) throws IllegalArgumentException {
        PlatformUser user = getUserById(platformUserId);
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }
        if (dto.getNewPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }
        PlatformUser persistedUser = getUserById(platformUserId);
        persistedUser.setPassword(dto.getNewPassword());
        userRepository.save(persistedUser);
    }

    @Transactional
    public PlatformUserDto updatePlatformUser(Long idUser, @Validated(ValidationGroup.OnUpdate.class) PlatformUserDto dto) throws ResourceNotFoundException {
        PlatformUser persistedUser = getUserById(idUser);
        persistPropertyManager.map(dto, persistedUser);
        userRepository.save(persistedUser);
        return persistPropertyManager.map(persistedUser, PlatformUserDto.class);
    }

    @Transactional
    public PlatformUserDto patchPlatformUser(Long idUser, @Valid PlatformUserDto dto) throws ResourceNotFoundException {
        PlatformUser persistedUser = getUserById(idUser);
        patchPropertyMapper.map(dto, persistedUser);
        userRepository.save(persistedUser);
        return persistPropertyManager.map(persistedUser, PlatformUserDto.class);
    }

    @Transactional
    public PlatformUserDto createUser(@Validated(ValidationGroup.OnInsert.class) PlatformUserDto dto) {
        PlatformUser persistedUser = persistPropertyManager.map(dto, PlatformUser.class);
        userRepository.save(persistedUser);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<PlatformUserDto> getAllPlatformUsers() {
        return userRepository.findAll(PlatformUserSpecs.hasNotBeenDeleted()).stream().map(
                currentPlatformUser -> persistPropertyManager.map(currentPlatformUser, PlatformUserDto.class)
        ).toList();
    }

    @Transactional(readOnly = true)
    protected PlatformUser getUserById(Long id) throws ResourceNotFoundException {
        return userRepository.findOne(
                PlatformUserSpecs.activeUserById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active user with id " + id + " was not found")
        );
    }

    @Transactional(readOnly = true)
    public PlatformUserDto getUserDtoById(Long id) throws ResourceNotFoundException {
        return persistPropertyManager.map(getUserById(id), PlatformUserDto.class);
    }
}
