package com.stellaTech.ecommerce.repository;

import com.stellaTech.ecommerce.model.productManagement.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, JpaSpecificationExecutor<Product>, PagingAndSortingRepository<Product, Long> {
}
