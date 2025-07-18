package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import com.stellaTech.ecommerce.service.dataDto.PlatformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dataDto.PlatformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.serviceDto.IdDtoResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatformUserService {
    @Autowired
    private PlatformUserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

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
    public PlatformUserDto updatePlatformUser(Long idUser, @Valid PlatformUserDto dto) throws ResourceNotFoundException {
        PlatformUser persistedUser = getUserById(idUser);
        userRepository.save(persistedUser);
        return modelMapper.map(persistedUser, PlatformUserDto.class);
    }

    @Transactional
    public PlatformUserDto patchPlatformUser(Long idUser, @Valid PlatformUserDto dto) throws ResourceNotFoundException {
        PlatformUser persistedUser = getUserById(idUser);
        modelMapper.map(dto, persistedUser);
        userRepository.save(persistedUser);
        return modelMapper.map(persistedUser, PlatformUserDto.class);
    }

    @Transactional
    public IdDtoResponse<PlatformUserDto> createUser(@Valid PlatformUserDto dto) {
        PlatformUser persistedUser = modelMapper.map(dto, PlatformUser.class);
        userRepository.save(persistedUser);
        return new IdDtoResponse<>(persistedUser.getId(), dto);
    }

    @Transactional(readOnly = true)
    public List<IdDtoResponse<PlatformUserDto>> getAllPlatformUsers() {
        return userRepository.findAll(PlatformUserSpecs.hasNotBeenDeleted()).stream().map(
                currentPlatformUser -> {
                    Long currentPlatformUserId = currentPlatformUser.getId();
                    PlatformUserDto platformUserDto = modelMapper.map(currentPlatformUser, PlatformUserDto.class);
                    return new IdDtoResponse<>(currentPlatformUserId, platformUserDto);
                }
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
        return modelMapper.map(getUserById(id), PlatformUserDto.class);
    }
}
