package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.orderManagement.CustomerOrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<CustomerOrderItem, Long> {
}
