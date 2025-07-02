package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.repository.PlatformUserIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/users")
    public PlatformUser createUser(@RequestBody PlatformUser newUser) {
        return userIdentity.save(newUser);
    }
}
