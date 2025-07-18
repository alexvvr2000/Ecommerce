package com.stellaTech.ecommerce.restController;

import com.stellaTech.ecommerce.service.PlatformUserService;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PasswordChangeDto;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PlatformUserDto;
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
    public List<PlatformUserDto> getAllUsers() {
        return userService.getAllPlatformUsers();
    }

    @GetMapping("/users/{idUser}")
    public ResponseEntity<PlatformUserDto> getUser(@NonNull @PathVariable Long idUser) {
        PlatformUserDto persistedUser = userService.getUserDtoById(idUser);
        return ResponseEntity.ok(persistedUser);
    }

    @PutMapping("/users/{idUser}")
    public ResponseEntity<PlatformUserDto> updateUser(
            @NonNull @PathVariable Long idUser,
            @Valid @RequestBody PlatformUserDto platformUserUpdateDto
    ) {
        PlatformUserDto savedUser = userService.updatePlatformUser(idUser, platformUserUpdateDto);
        return ResponseEntity.ok(savedUser);
    }

    @PatchMapping("/users/{idUser}")
    public ResponseEntity<PlatformUserDto> partialUpdateUser(
            @NonNull @PathVariable Long idUser,
            @Valid @RequestBody PlatformUserDto platformUserPatchDto
    ) {
        PlatformUserDto updatedUser = userService.patchPlatformUser(idUser, platformUserPatchDto);
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
    public ResponseEntity<PlatformUserDto> createUser(@Valid @RequestBody PlatformUserDto platformUserInsertDto) {
        PlatformUserDto persistedUser = userService.createUser(platformUserInsertDto);
        return ResponseEntity.ok(persistedUser);
    }
}
