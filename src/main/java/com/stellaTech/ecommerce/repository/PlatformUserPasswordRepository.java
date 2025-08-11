package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUserPassword;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformUserPasswordRepository extends CrudRepository<PlatformUserPassword, Long>, JpaSpecificationExecutor<PlatformUserPassword> {
    default PlatformUserPassword getPasswordByUserId(Long platformUserId) throws ResourceNotFoundException {
        Optional<PlatformUserPassword> passwordObject = findOne(
                PlatformUserSpecs.activeUserPasswordById(platformUserId)
        );
        if (passwordObject.isEmpty()) {
            throw new ResourceNotFoundException("The user whose password was searched was not found");
        }
        return passwordObject.get();
    }
}
