package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.dto.platformUser.PlatformUserInsertDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserMapper;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserPatchDto;
import com.stellaTech.ecommerce.dto.platformUser.PlatformUserUpdateDto;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatformUserService {
    @Autowired
    private PlatformUserRepository userRepository;

    private PlatformUserMapper platformUserMapper;

    @Transactional
    public void logicallyDeleteUser(Long id) throws ResourceNotFoundException {
        PlatformUser user = getUserById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(@NotEmpty @NotNull String newPassword, @NotNull Long platformUserId){
        PlatformUser persistedUser = getUserById(platformUserId);
        persistedUser.setPassword(newPassword);
        userRepository.save(persistedUser);
    }

    @Transactional
    public PlatformUser updatePlatformUser(Long idUser, PlatformUserUpdateDto updatedData) throws ResourceNotFoundException {
        PlatformUser existingUser = getUserById(idUser);
        PlatformUser updatedUser = platformUserMapper.updatePlatformUserFromDto(existingUser, updatedData);
        return userRepository.save(updatedUser);
    }

    @Transactional
    public PlatformUser patchPlatformUser(Long idUpdatedUser, PlatformUserPatchDto newUserValues) throws ResourceNotFoundException {
        PlatformUser oldPlatformUser = getUserById(idUpdatedUser);
        PlatformUser newPlatformUser = platformUserMapper.patchPlatformUserFromDto(oldPlatformUser, newUserValues);
        return userRepository.save(newPlatformUser);
    }

    @Transactional
    public PlatformUser createUser(PlatformUserInsertDto newUser) {
        PlatformUser persistedUser = platformUserMapper.createPlatformUserEntity(newUser);
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
