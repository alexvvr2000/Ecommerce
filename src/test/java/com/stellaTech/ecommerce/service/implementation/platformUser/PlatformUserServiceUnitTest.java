package com.stellaTech.ecommerce.service.implementation.platformUser;

import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.exception.instance.InvalidPasswordField;
import com.stellaTech.ecommerce.exception.instance.RepeatedUserPassword;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUserPassword;
import com.stellaTech.ecommerce.repository.PlatformUserPasswordRepository;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.implementation.PlatformUserServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlatformUserServiceUnitTest {

    protected final DataGenerationService dataGenerationService = new DataGenerationService();
    protected final Long testUserId = 1L;

    @Mock
    protected PlatformUserRepository userRepository;

    @Mock
    protected PlatformUserPasswordRepository userPasswordRepository;

    @Mock
    @Qualifier("persistPropertyMapper")
    protected ModelMapper persistPropertyMapper;

    @Mock
    @Qualifier("patchPropertyMapper")
    protected ModelMapper patchPropertyMapper;

    @InjectMocks
    protected PlatformUserServiceImp platformUserService;

    @Test
    void createUserWithValidData() throws NoSuchFieldException, IllegalAccessException {
        PlatformUserDto validUserDto = dataGenerationService.createValidPlatformUserDto(null);
        PlatformUser userModel = dataGenerationService.createPlatformUserModel(validUserDto);

        when(userRepository.save(any(PlatformUser.class))).thenReturn(userModel);
        when(persistPropertyMapper.map(any(PlatformUserDto.class), eq(PlatformUser.class)))
                .thenReturn(userModel);
        when(persistPropertyMapper.map(any(PlatformUser.class), eq(PlatformUserDto.class)))
                .thenReturn(validUserDto);

        PlatformUserDto result = platformUserService.createUser(validUserDto);

        assertNotNull(result);
        assertEquals(validUserDto.getEmail(), result.getEmail());
        assertEquals(validUserDto.getFullName(), result.getFullName());
    }

    @Test
    void getUserDtoByIdWithExistingUser() throws ResourceNotFoundException, NoSuchFieldException, IllegalAccessException {
        PlatformUserDto expectedUserDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUser userModel = dataGenerationService.createPlatformUserModel(expectedUserDto);

        when(userRepository.getUserById(testUserId)).thenReturn(userModel);
        when(persistPropertyMapper.map(userModel, PlatformUserDto.class)).thenReturn(expectedUserDto);

        PlatformUserDto result = platformUserService.getUserDtoById(testUserId);

        assertNotNull(result);
        assertEquals(expectedUserDto.getId(), result.getId());
        assertEquals(expectedUserDto.getEmail(), result.getEmail());
    }

    @Test
    void getUserDtoByIdWithNonExistingUser() {
        when(userRepository.getUserById(testUserId)).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> {
            platformUserService.getUserDtoById(testUserId);
        });
    }

    @Test
    void getAllPlatformUsers() throws NoSuchFieldException, IllegalAccessException {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<PlatformUser> userList = new ArrayList<>();

        for (int i = 1; i <= pageSize; i++) {
            PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto((long) i);
            PlatformUser userModel = dataGenerationService.createPlatformUserModel(userDto);
            userList.add(userModel);

            when(persistPropertyMapper.map(userModel, PlatformUserDto.class)).thenReturn(userDto);
        }

        Page<PlatformUser> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findAll(PlatformUserSpecs.hasNotBeenDeleted(), pageable)).thenReturn(userPage);

        Page<PlatformUserDto> result = platformUserService.getAllPlatformUsers(pageable);

        assertNotNull(result);
        assertEquals(pageSize, result.getContent().size());
        assertEquals(userList.size(), result.getTotalElements());
    }

    @Test
    void changePasswordWithValidData() throws ResourceNotFoundException, RepeatedUserPassword, InvalidPasswordField, NoSuchFieldException, IllegalAccessException {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);

        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword(userDto.getPassword())
                .newPassword("newPassword456")
                .confirmNewPassword("newPassword456")
                .build();

        PlatformUserPassword currentPassword = new PlatformUserPassword();
        currentPassword.setPassword(userDto.getPassword());

        when(userPasswordRepository.getPasswordByUserId(testUserId)).thenReturn((currentPassword));

        platformUserService.changePassword(passwordChangeDto, testUserId);
    }

    @Test
    void changePasswordWithNonExistingUser() {
        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .confirmNewPassword("newPassword")
                .build();

        when(userPasswordRepository.getPasswordByUserId(testUserId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            platformUserService.changePassword(passwordChangeDto, testUserId);
        });
    }

    @Test
    void changePasswordWithRepeatedPassword() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);

        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword(userDto.getPassword())
                .newPassword(userDto.getPassword())
                .confirmNewPassword(userDto.getPassword())
                .build();

        PlatformUserPassword currentPassword = new PlatformUserPassword();
        currentPassword.setPassword(userDto.getPassword());

        when(userPasswordRepository.getPasswordByUserId(testUserId)).thenReturn(currentPassword);

        assertThrows(RepeatedUserPassword.class, () -> {
            platformUserService.changePassword(passwordChangeDto, testUserId);
        });
    }

    @Test
    void changePasswordWithInvalidOldPassword() throws ResourceNotFoundException, RepeatedUserPassword, InvalidPasswordField, NoSuchFieldException, IllegalAccessException {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);

        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword("invalidOldPassword")
                .newPassword("newPassword456")
                .confirmNewPassword("newPassword456")
                .build();

        PlatformUserPassword currentPassword = new PlatformUserPassword();
        currentPassword.setPassword(userDto.getPassword());

        when(userPasswordRepository.getPasswordByUserId(testUserId)).thenReturn((currentPassword));

        assertThrows(InvalidPasswordField.class, () -> platformUserService.changePassword(passwordChangeDto, testUserId));
    }

    @Test
    void changePasswordWithPasswordMismatch() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);

        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword(userDto.getPassword())
                .newPassword("newPassword")
                .confirmNewPassword("PasswordMismatch")
                .build();

        PlatformUserPassword currentPassword = new PlatformUserPassword();
        currentPassword.setPassword(userDto.getPassword());

        when(userPasswordRepository.getPasswordByUserId(testUserId)).thenReturn((currentPassword));

        assertThrows(InvalidPasswordField.class, () -> platformUserService.changePassword(passwordChangeDto, testUserId));
    }
}
