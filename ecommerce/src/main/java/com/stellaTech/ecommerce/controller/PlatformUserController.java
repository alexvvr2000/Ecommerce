package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.dto.PlatformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.service.PlatformUserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class PlatformUserController {
    @Autowired
    private PlatformUserService userService;

    @GetMapping("/users")
    public List<PlatformUser> getAllUsers() {
        return userService.getAllPlatformUsers();
    }

    @GetMapping("/users/{idUser}")
    public PlatformUser getUser(@NonNull @PathVariable Long idUser) {
        return userService.getUserById(idUser);
    }

    @PutMapping("/users/{idUser}")
    public ResponseEntity<PlatformUser> updateUser(
            @NonNull @PathVariable Long idUser,
            @Valid @RequestBody PlatformUserUpdateDto platformUserUpdateDto
    ) {
        PlatformUser savedUser = userService.updatePlatformUser(idUser, platformUserUpdateDto);
        return ResponseEntity.ok(savedUser);
    }

    @PatchMapping("/users/{idUser}")
    public ResponseEntity<PlatformUser> partialUpdateUser(
            @NonNull @PathVariable Long idUser,
            @Valid @RequestBody PlatformUserPatchDto platformUserPatchDto
    ) {
        PlatformUser updatedUser = userService.patchPlatformUser(idUser, platformUserPatchDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/users/{idUser}/password")
    public ResponseEntity<?> changePassword(@NonNull @PathVariable Long idUser, @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto, idUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{idUser}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long idUser) {
        userService.logicallyDeleteUser(idUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users")
    public PlatformUser createUser(@Valid @RequestBody PlatformUserInsertDto platformUserInsertDto) {
        return userService.createUser(platformUserInsertDto);
    }
}
