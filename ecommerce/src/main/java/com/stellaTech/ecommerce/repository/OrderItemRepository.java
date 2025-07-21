package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.orderManagement.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<CustomerOrderItem, Long> {
}
