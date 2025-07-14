package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.dto.mapper.OrderMapper;
import com.stellaTech.ecommerce.dto.order.OrderInsertDto;
import com.stellaTech.ecommerce.dto.order.OrderSelectDto;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PlatformUserService platformUserService;

    @Transactional
    public void logicallyDeleteOrder(Long id) throws ResourceNotFoundException {
        Order order = getOrderById(id);
        order.setDeleted(true);
    }

    @Transactional
    public OrderSelectDto createOrder(@Valid OrderInsertDto dto) throws ResourceNotFoundException {
        Order newOrder = orderMapper.createOrderEntity(dto);
        Order persistedOrder = orderRepository.save(newOrder);
        return orderMapper.summaryOrder(persistedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderSelectDto> getAllOrders() {
        return orderRepository.findAll(
                OrderSpecs.isNotDeleted()
        ).stream().map(orderMapper::summaryOrder).toList();
    }

    @Transactional(readOnly = true)
    private Order getOrderById(Long id) throws ResourceNotFoundException {
        return orderRepository.findOne(
                OrderSpecs.hasNotBeenDeleted(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }

    public OrderSelectDto getOrderDtoById(Long id) {
        Order persistedOrder = getOrderById(id);
        return orderMapper.summaryOrder(persistedOrder);
    }
}
