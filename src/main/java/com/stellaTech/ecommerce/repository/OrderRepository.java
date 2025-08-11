package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<CustomerOrder, Long>, JpaSpecificationExecutor<CustomerOrder>, PagingAndSortingRepository<CustomerOrder, Long> {
    default CustomerOrder getOrderById(Long id) throws ResourceNotFoundException {
        return findOne(
                OrderSpecs.hasNotBeenDeleted(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active order with id " + id + " was not found")
        );
    }
}
