package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.service.dto.platformUser.PlatformUserInsertDto;
import com.stellaTech.ecommerce.service.dto.platformUser.PlatformUserMapper;
import com.stellaTech.ecommerce.service.dto.platformUser.PlatformUserPatchDto;
import com.stellaTech.ecommerce.service.dto.platformUser.PlatformUserUpdateDto;
import com.stellaTech.ecommerce.service.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.service.specification.PlatformUserSpecs;
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
    public void logicalDeleteUser(Long id) throws ResourceNotFoundException {
        PlatformUser user = getUserById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Transactional
    public PlatformUser updateEntireUser(Long idUser, PlatformUserInsertDto updatedUser) throws ResourceNotFoundException {
        PlatformUser existingUser = getUserById(idUser);
        return userRepository.save(existingUser);
    }

    @Transactional
    public PlatformUser updateUserPartially(Long idUpdatedUser, PlatformUserUpdateDto newUserValues) throws ResourceNotFoundException {
        PlatformUser oldPlatformUser = getUserById(idUpdatedUser);
        return platformUserMapper.updatePlatformUserFromDto(oldPlatformUser, newUserValues);
    }

    @Transactional
    public PlatformUser createUser(PlatformUserInsertDto newUser) {
        PlatformUser persistedUser = platformUserMapper.createPlatformUserEntity(newUser);
        return userRepository.save(persistedUser);
    }

    @Transactional(readOnly = true)
    public List<PlatformUser> getAllActiveUsers() {
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
