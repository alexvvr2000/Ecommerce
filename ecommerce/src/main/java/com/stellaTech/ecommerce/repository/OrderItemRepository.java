package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.OrderManagement.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
