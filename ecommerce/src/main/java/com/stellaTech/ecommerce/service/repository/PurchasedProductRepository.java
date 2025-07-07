package com.stellaTech.ecommerce.service.repository;

import com.stellaTech.ecommerce.model.PurchasedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasedProductRepository extends JpaRepository<PurchasedProduct, Long>, JpaSpecificationExecutor<PurchasedProduct> {
}
