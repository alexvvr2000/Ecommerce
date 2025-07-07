package com.stellaTech.ecommerce.service.repository;

import com.stellaTech.ecommerce.model.Order;
import com.stellaTech.ecommerce.model.OrderPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, OrderPK>, JpaSpecificationExecutor<Order> {
}
