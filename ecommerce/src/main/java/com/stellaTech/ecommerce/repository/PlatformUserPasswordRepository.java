package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUserPassword;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformUserPasswordRepository extends CrudRepository<PlatformUserPassword, Long>, JpaSpecificationExecutor<PlatformUserPassword> {
}
