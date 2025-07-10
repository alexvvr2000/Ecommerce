package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformUserRepository extends JpaRepository<PlatformUser, Long>, JpaSpecificationExecutor<PlatformUser> {
}
