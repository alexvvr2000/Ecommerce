package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.repository.PlatformUserIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class PlatformUserController {
    @Autowired
    private PlatformUserIdentity userIdentity;

    @GetMapping("/users")
    public List<PlatformUser> getAllUsers() {
        return userIdentity.findAll();
    }

    @GetMapping("/users/{idUser}")
    public ResponseEntity<PlatformUser> getUser(@PathVariable Long idUser) {
        Optional<PlatformUser> user = userIdentity.findById(idUser);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{idUser}")
    public ResponseEntity<PlatformUser> updateUser(
            @PathVariable Long idUser,
            @RequestBody PlatformUser updatedUser
    ) {
        PlatformUser existingUser = userIdentity.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("user with ID " + idUser + " not found"));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setRfc(updatedUser.getRfc());
        existingUser.setDeleted(updatedUser.getDeleted());

        PlatformUser savedUser = userIdentity.save(existingUser);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/users")
    public PlatformUser createUser(@RequestBody PlatformUser newUser) {
        return userIdentity.save(newUser);
    }
}
