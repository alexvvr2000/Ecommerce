package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<CustomerOrder, Long>, JpaSpecificationExecutor<CustomerOrder>, PagingAndSortingRepository<CustomerOrder, Long> {
}
