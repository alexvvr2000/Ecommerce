package com.stellaTech.ecommerce.restController;

import com.stellaTech.ecommerce.service.platformUser.PlatformUserService;
import com.stellaTech.ecommerce.service.dto.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PlatformUserDto;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class PlatformUserController {
    @Autowired
    private PlatformUserService userService;

    @GetMapping("/users")
    public Page<PlatformUserDto> getAllUsers(
            @SortDefault("id")
            @NonNull final Pageable pageable
    ) {
        log.info("Showing a page of orders");
        return userService.getAllPlatformUsers(pageable);
    }

    @GetMapping("/users/{idUser}")
    public ResponseEntity<PlatformUserDto> getUser(@NonNull @PathVariable Long idUser) {
        PlatformUserDto persistedUser = userService.getUserDtoById(idUser);
        log.info("Read user with id {}", idUser);
        return ResponseEntity.ok(persistedUser);
    }

    @PutMapping("/users/{idUser}")
    public ResponseEntity<PlatformUserDto> updateUser(
            @NonNull @PathVariable Long idUser,
            @Validated(NullCheckGroup.OnUpdate.class) @RequestBody PlatformUserDto platformUserUpdateDto
    ) {
        PlatformUserDto savedUser = userService.updatePlatformUser(idUser, platformUserUpdateDto);
        log.info("Updated user with id {}", idUser);
        return ResponseEntity.ok(savedUser);
    }

    @PatchMapping("/users/{idUser}")
    public ResponseEntity<PlatformUserDto> patchUser(
            @NonNull @PathVariable Long idUser,
            @Valid @RequestBody PlatformUserDto platformUserPatchDto
    ) {
        PlatformUserDto updatedUser = userService.patchPlatformUser(idUser, platformUserPatchDto);
        log.info("Patched user with id {}", idUser);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{idUser}/password")
    public ResponseEntity<?> changePassword(@NonNull @PathVariable Long idUser, @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto, idUser);
        log.info("Changed password for user with id {}", idUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{idUser}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long idUser) {
        userService.logicallyDeleteUser(idUser);
        log.info("Deleted user with id {}", idUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users")
    public ResponseEntity<PlatformUserDto> createUser(@Validated({NullCheckGroup.OnInsert.class}) @RequestBody PlatformUserDto platformUserInsertDto) {
        PlatformUserDto persistedUser = userService.createUser(platformUserInsertDto);
        log.info("New user with id {}", persistedUser.getId());
        return ResponseEntity.ok(persistedUser);
    }
}
