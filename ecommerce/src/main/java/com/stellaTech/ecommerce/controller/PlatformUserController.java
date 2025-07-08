package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.service.PlatformUserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            @NonNull @RequestBody PlatformUser updatedUser
    ) {
        PlatformUser savedUser = userService.updateEntireUser(idUser, updatedUser);
        return ResponseEntity.ok(savedUser);
    }

    @PatchMapping("/users/{idUser}")
    public ResponseEntity<PlatformUser> partialUpdateUser(
            @NonNull @PathVariable Long idUser,
            @NonNull @RequestBody Map<String, Object> updatedFields // recibir el platformUser
    ) {
        // revisar cuales campos que son nulos y luego actualizarlo si no lo son
        PlatformUser savedUser = userService.updateUserPartially(idUser, updatedFields);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/users/{idUser}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long idUser) {
        try {
            userService.logicalDeleteUser(idUser);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Error("Internal server error"));
        }
    }

    @PostMapping("/users")
    public PlatformUser createUser(@NonNull @RequestBody PlatformUser newUser) {
        return userService.createUser(newUser);
    }
}
