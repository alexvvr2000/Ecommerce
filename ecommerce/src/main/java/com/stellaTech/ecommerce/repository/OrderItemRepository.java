package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.orderManagement.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
