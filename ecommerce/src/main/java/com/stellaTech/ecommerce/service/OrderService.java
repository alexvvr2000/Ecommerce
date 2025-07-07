package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.Order;
import com.stellaTech.ecommerce.model.OrderPK;
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
    public OrderPK logicalDeleteOrder(OrderPK id) throws Exception {
        Order order = getOrderById(id);
        order.setDeleted(true);
        return id;
    }

    @Transactional
    public Order createOrder(Order newOrder) {
        return orderRepository.save(newOrder);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllActiveOrders() {
        return orderRepository.findAll(OrderSpecs.isNotDeleted());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(OrderPK id) throws ResourceNotFoundException {
        return orderRepository.findOne(
                OrderSpecs.orderIsActive(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }
}
