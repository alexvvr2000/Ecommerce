package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformUserIdentity extends JpaRepository<PlatformUser, Long> {
}
