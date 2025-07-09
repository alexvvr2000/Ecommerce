package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.DuplicatedResourceException;
import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.service.repository.OrderRepository;
import com.stellaTech.ecommerce.service.specification.OrderSpecs;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PlatformUserService platformUserService;

    @Transactional
    public void logicalDeleteOrder(Long id) throws ResourceNotFoundException {
        Order order = getOrderById(id);
        order.setDeleted(true);
    }

    @Transactional
    public Order createOrder(orderDTO newOrder) throws DuplicatedResourceException, InvalidInputException {
        return null;
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

    @Value
    public static class orderDTO {
        Long userId;
        List<OrderItemDTO> items;

        @Value
        public static class OrderItemDTO {
            Long productId;
            int productCount;
        }
    }
}
