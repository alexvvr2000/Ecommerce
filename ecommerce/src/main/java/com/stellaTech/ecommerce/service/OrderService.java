package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.Order;
import com.stellaTech.ecommerce.service.repository.OrderRepository;
import com.stellaTech.ecommerce.service.specification.OrderSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    protected OrderRepository orderRepository;

    @Transactional
    public Long logicalDeleteOrder(Long id) throws Exception {
        Order product = getOrderById(id);
        product.setDeleted(true);
        return id;
    }

    @Transactional
    public Order createOrder(Order newProduct) {
        return orderRepository.save(newProduct);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllActiveOrder() {
        return orderRepository.findAll(OrderSpecs.isNotDeleted());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) throws ResourceNotFoundException {
        return orderRepository.findOne(
                OrderSpecs.activeProductById(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }
}
