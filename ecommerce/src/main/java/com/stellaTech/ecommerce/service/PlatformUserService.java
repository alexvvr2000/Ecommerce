package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.service.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.service.specification.PlatformUserSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PlatformUserService {
    @Autowired
    private PlatformUserRepository userRepository;

    @Transactional
    public Long logicalDeleteUser(Long id) throws Exception {
        PlatformUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.isDeleted()) {
            throw new Exception("User already deleted");
        }
        user.setDeleted(true);
        userRepository.save(user);
        return id;
    }

    @Transactional
    public PlatformUser updateEntireUser(Long idUser, PlatformUser updatedUser) {
        PlatformUser existingUser = userRepository.findById(idUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with id " + idUser + " was not found"));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setRfc(updatedUser.getRfc());

        return userRepository.save(existingUser);
    }

    @Transactional
    public PlatformUser updateUserPartially(Long id, Map<String, Object> updatedFields) {
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
        return userRepository.save(platformUser);
    }

    @Transactional
    public PlatformUser createUser(PlatformUser newUser) {
        return userRepository.save(newUser);
    }

    @Transactional
    public PlatformUser markUserAsDeleted(Long id) {
        PlatformUser user = getUserById(id);
        user.setDeleted(true);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<PlatformUser> getAllActiveUsers() {
        return userRepository.findAll(PlatformUserSpecs.isNotDeleted());
    }

    @Transactional(readOnly = true)
    public PlatformUser getUserById(Long id) {
        return userRepository.findOne(
                PlatformUserSpecs.activeUserById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active user with id " + id + " was not found")
        );
    }
}
