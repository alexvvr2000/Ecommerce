package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.dto.mapper.OrderMapper;
import com.stellaTech.ecommerce.dto.order.OrderInsertDto;
import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.model.OrderManagement.OrderItem;
import com.stellaTech.ecommerce.model.PlatformUser;
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

    @Autowired
    private ProductService productService;

    @Transactional
    public void logicallyDeleteOrder(Long id) throws ResourceNotFoundException {
        Order order = getOrderById(id);
        order.setDeleted(true);
    }

    @Transactional
    public Order createOrder(@Valid OrderInsertDto dto) throws InvalidInputException, ResourceNotFoundException {
        PlatformUser user = platformUserService.getUserById(dto.getPlatformUserId());
        Order order = new Order(user);
        for (OrderInsertDto.OrderItemInsertDto itemInsertDto : dto.getItems()) {
            OrderItem item = orderMapper.createOrderItemEntity(itemInsertDto);
            order.addOrderItem(item);
        }
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
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
