package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformUserRepository extends CrudRepository<PlatformUser, Long>, JpaSpecificationExecutor<PlatformUser>, PagingAndSortingRepository<PlatformUser, Long> {
}
