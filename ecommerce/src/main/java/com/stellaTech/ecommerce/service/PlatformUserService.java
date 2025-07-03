package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.service.repository.PlatformUserIdentity;
import com.stellaTech.ecommerce.service.specification.PlatformUserSpecs;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlatformUserService {
    @Autowired
    private PlatformUserIdentity userIdentity;

    public List<PlatformUser> getAllUsers() {
        return userIdentity.findAll(PlatformUserSpecs.isNotDeleted());
    }

    @Transactional
    public Long logicalDeleteUser(Long id) throws Exception {
        PlatformUser user = userIdentity.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.isDeleted()) {
            throw new Exception("User already deleted");
        }
        user.setDeleted(true);
        userIdentity.save(user);
        return id;
    }

    @Transactional
    public PlatformUser updateEntireUser(Long idUser, PlatformUser updatedUser) {
        PlatformUser existingUser = userIdentity.findById(idUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with id " + idUser + " was not found"));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setRfc(updatedUser.getRfc());

        return userIdentity.save(existingUser);
    }

    @Transactional
    public PlatformUser updateUserPartially(Long id, Map<String, Object> updatedFields){
        PlatformUser platformUser = getUserById(id);
        updatedFields.forEach((key, value) -> {
            switch (key) {
                case "fullName":
                    platformUser.setFullName((String) value);
                    break;
                case "email":
                    platformUser.setEmail((String) value);
                    break;
                case "phoneNumber":
                    platformUser.setPhoneNumber((String) value);
                    break;
                case "rfc":
                    platformUser.setRfc((String) value);
                    break;
            }
        });
        return userIdentity.save(platformUser);
    }

    @Transactional
    public PlatformUser createUser(PlatformUser newUser) {
        return userIdentity.save(newUser);
    }

    @Transactional
    public PlatformUser markUserAsDeleted(Long id) {
        PlatformUser user = getUserById(id);
        user.setDeleted(true);
        return userIdentity.save(user);
    }

    public List<PlatformUser> getAllActiveUsers() {
        return userIdentity.findAll(PlatformUserSpecs.isNotDeleted());
    }

    public PlatformUser getUserById(Long id) {
        return userIdentity.findOne(
                PlatformUserSpecs.activeUserById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active user with id " + id + " was not found")
        );
    }
}
