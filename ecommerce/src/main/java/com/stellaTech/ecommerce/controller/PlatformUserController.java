package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.service.PlatformUserService;
import com.stellaTech.ecommerce.service.dto.platformUser.PlatformUserInsertDto;
import com.stellaTech.ecommerce.service.dto.platformUser.PlatformUserPatchDto;
import com.stellaTech.ecommerce.service.dto.platformUser.PlatformUserUpdateDto;
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
        return userService.getAllActiveUsers();
    }

    @GetMapping("/users/{idUser}")
    public PlatformUser getUser(@NonNull @PathVariable Long idUser) {
        return userService.getUserById(idUser);
    }

    @PutMapping("/users/{idUser}")
    public ResponseEntity<PlatformUser> updateUser(
            @NonNull @PathVariable Long idUser,
            @NonNull @RequestBody PlatformUserUpdateDto updatedUser
    ) {
        PlatformUser savedUser = userService.updateEntireUser(idUser, updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    @PatchMapping("/users/{idUser}")
    public ResponseEntity<?> partialUpdateUser(
            @NonNull @PathVariable Long idUser,
            @NonNull @RequestBody PlatformUserPatchDto updatedFields
    ) {
        PlatformUser updatedUser = userService.updateUserPartially(idUser, updatedFields);
        return ResponseEntity.ok(updatedFields);
    }

    @DeleteMapping("/users/{idUser}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long idUser) {
        userService.logicalDeleteUser(idUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users")
    public PlatformUser createUser(@NonNull @RequestBody PlatformUserInsertDto newUser) {
        return userService.createUser(newUser);
    }
}
