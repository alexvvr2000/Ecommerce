package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.repository.specification.PlatformUserSpecs;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformUserRepository extends CrudRepository<PlatformUser, Long>, JpaSpecificationExecutor<PlatformUser>, PagingAndSortingRepository<PlatformUser, Long> {
    default PlatformUser getUserById(Long id) throws ResourceNotFoundException {
        return findOne(
                PlatformUserSpecs.activeUserById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active user with id " + id + " was not found")
        );
    }
}
