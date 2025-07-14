package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.dto.mapper.PlatformUserMapper;
import com.stellaTech.ecommerce.dto.platformUser.PasswordChangeDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserInsertDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserPatchDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserUpdateDto;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatformUserService {
    @Autowired
    private PlatformUserRepository userRepository;

    @Autowired
    private PlatformUserMapper platformUserMapper;

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
        PlatformUser updatedPasswordUser = platformUserMapper.patchPlatformUserPassword(user, dto);
        userRepository.save(updatedPasswordUser);
    }

    @Transactional
    public PlatformUser updatePlatformUser(Long idUser, PlatformUserUpdateDto dto) throws ResourceNotFoundException {
        PlatformUser existingUser = getUserById(idUser);
        PlatformUser updatedUser = platformUserMapper.updatePlatformUserFromDto(existingUser, dto);
        return userRepository.save(updatedUser);
    }

    @Transactional
    public PlatformUser patchPlatformUser(Long idUpdatedUser, PlatformUserPatchDto dto) throws ResourceNotFoundException {
        PlatformUser oldPlatformUser = getUserById(idUpdatedUser);
        PlatformUser newPlatformUser = platformUserMapper.patchPlatformUserFromDto(oldPlatformUser, dto);
        return userRepository.save(newPlatformUser);
    }

    @Transactional
    public PlatformUser createUser(PlatformUserInsertDto dto) {
        PlatformUser persistedUser = platformUserMapper.createPlatformUserEntity(dto);
        return userRepository.save(persistedUser);
    }

    @Transactional(readOnly = true)
    public List<PlatformUser> getAllPlatformUsers() {
        return userRepository.findAll(PlatformUserSpecs.hasNotBeenDeleted());
    }

    @Transactional(readOnly = true)
    public PlatformUser getUserById(Long id) throws ResourceNotFoundException {
        return userRepository.findOne(
                PlatformUserSpecs.activeUserById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active user with id " + id + " was not found")
        );
    }
}
