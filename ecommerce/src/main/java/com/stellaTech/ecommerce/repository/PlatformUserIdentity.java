package com.stellaTech.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stellaTech.ecommerce.model.PlatformUser;

@Repository
public interface PlatformUserIdentity extends JpaRepository<PlatformUser, Long> {
}
