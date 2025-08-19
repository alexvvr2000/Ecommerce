package com.stellaTech.ecommerce.service.integrationTest;

import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.exception.instance.InvalidPasswordField;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.generics.PlatformUserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlatformUserServiceIntegrationTest {
    protected final Long testUserId = 1L;
    protected final Long nonExistentUserId = 999L;
    @Autowired
    protected PlatformUserService platformUserService;
    @Autowired
    protected DataGenerationService dataGenerationService;

    @Test
    void createUserWithValidData() {
        // Arrange
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);

        // Act
        PlatformUserDto createdUser = platformUserService.createUser(userDto);

        // Assert
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
        assertEquals(userDto.getFullName(), createdUser.getFullName());
        assertEquals(userDto.getPhoneNumber(), createdUser.getPhoneNumber());
    }

    @Test
    void getAllPlatformUsersReturnsPaginatedResults() {
        PlatformUserDto user1 = platformUserService.createUser(
                dataGenerationService.createValidPlatformUserDto(1L)
        );
        PlatformUserDto user2 = platformUserService.createUser(
                dataGenerationService.createValidPlatformUserDto(2L)
        );

        Page<PlatformUserDto> firstPage = platformUserService.getAllPlatformUsers(
                PageRequest.of(0, 1, Sort.by("id"))
        );
        Page<PlatformUserDto> secondPage = platformUserService.getAllPlatformUsers(
                PageRequest.of(1, 1, Sort.by("id"))
        );

        assertEquals(1, firstPage.getContent().size());
        assertEquals(1, secondPage.getContent().size());
        assertTrue(firstPage.getTotalElements() >= 2);
    }

    @Test
    void getUserDtoByIdWhenUserExists() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto createdUser = platformUserService.createUser(userDto);

        PlatformUserDto retrievedUser = platformUserService.getUserDtoById(createdUser.getId());

        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
        assertEquals(createdUser.getFullName(), retrievedUser.getFullName());
    }

    @Test
    void getUserDtoByIdWhenUserDoesNotExist() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                platformUserService.getUserDtoById(nonExistentUserId)
        );
    }

    @Test
    void changePasswordWithValidData() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto createdUser = platformUserService.createUser(userDto);
        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword(userDto.getPassword()) // Assuming this is the default password from DataGenerationService
                .newPassword("newSecurePassword456")
                .confirmNewPassword("newSecurePassword456")
                .build();

        assertDoesNotThrow(() ->
                platformUserService.changePassword(passwordChangeDto, createdUser.getId())
        );
    }

    @Test
    void changePasswordWithInvalidOldPassword() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto createdUser = platformUserService.createUser(userDto);

        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword("wrongOldPassword")
                .newPassword("newPassword123")
                .confirmNewPassword("newPassword123")
                .build();

        assertThrows(InvalidPasswordField.class, () ->
                platformUserService.changePassword(passwordChangeDto, createdUser.getId())
        );
    }

    @Test
    void changePasswordWithMismatchedNewPasswords() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto createdUser = platformUserService.createUser(userDto);

        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword("oldPassword123")
                .newPassword("newPassword123")
                .confirmNewPassword("differentPassword456")
                .build();

        assertThrows(InvalidPasswordField.class, () ->
                platformUserService.changePassword(passwordChangeDto, createdUser.getId())
        );
    }

    @Test
    void changePasswordWithRepeatedPassword() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto createdUser = platformUserService.createUser(userDto);

        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword("oldPassword123")
                .newPassword("oldPassword123")
                .confirmNewPassword("oldPassword123")
                .build();

        assertThrows(InvalidPasswordField.class, () ->
                platformUserService.changePassword(passwordChangeDto, createdUser.getId())
        );
    }

    @Test
    void changePasswordForNonExistentUser() {
        // Arrange
        PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                .oldPassword("anyPassword")
                .newPassword("newPassword")
                .confirmNewPassword("newPassword")
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                platformUserService.changePassword(passwordChangeDto, nonExistentUserId)
        );
    }

    @Test
    void logicallyDeleteUserById() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto createdUser = platformUserService.createUser(userDto);
        Long userId = createdUser.getId();

        assertNotNull(platformUserService.getUserDtoById(userId));

        platformUserService.logicallyDeleteById(userId);

        assertThrows(ResourceNotFoundException.class, () ->
                platformUserService.getUserDtoById(userId)
        );
    }

    @Test
    void logicallyDeleteNonExistentUser() {
        assertThrows(ResourceNotFoundException.class, () ->
                platformUserService.logicallyDeleteById(nonExistentUserId)
        );
    }

    @Test
    void getAllPlatformUsersExcludesLogicallyDeletedUsers() {
        PlatformUserDto activeUser = platformUserService.createUser(
                dataGenerationService.createValidPlatformUserDto(1L)
        );
        PlatformUserDto userToDelete = platformUserService.createUser(
                dataGenerationService.createValidPlatformUserDto(2L)
        );

        platformUserService.logicallyDeleteById(userToDelete.getId());

        Page<PlatformUserDto> allUsers = platformUserService.getAllPlatformUsers(
                Pageable.unpaged()
        );

        assertTrue(allUsers.getContent().stream().anyMatch(
                        user -> user.getId().equals(activeUser.getId())
                )
        );
        assertTrue(allUsers.getContent().stream().noneMatch(
                        user -> user.getId().equals(userToDelete.getId())
                )
        );
    }
}
