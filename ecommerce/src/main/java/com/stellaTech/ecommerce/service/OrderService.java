package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.model.OrderManagement.OrderItem;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.service.dto.order.OrderInsertDto;
import com.stellaTech.ecommerce.service.dto.order.OrderMapper;
import com.stellaTech.ecommerce.service.repository.OrderRepository;
import com.stellaTech.ecommerce.service.specification.OrderSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    private OrderMapper orderMapper;

    @Transactional
    public void logicalDeleteOrder(Long id) throws ResourceNotFoundException {
        Order order = getOrderById(id);
        order.setDeleted(true);
    }

    @Transactional
    public Order createOrder(OrderInsertDto newOrder) throws InvalidInputException, ResourceNotFoundException {
        Order order = orderMapper.toOrder(newOrder);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllActiveOrders() {
        return orderRepository.findAll(OrderSpecs.isNotDeleted());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) throws ResourceNotFoundException {
        return orderRepository.findOne(
                OrderSpecs.hasNotBeenDeleted(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }

}
