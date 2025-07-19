package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformUserPasswordRepository extends JpaRepository<PlatformUserPassword, Long>, JpaSpecificationExecutor<PlatformUserPassword> {
}
