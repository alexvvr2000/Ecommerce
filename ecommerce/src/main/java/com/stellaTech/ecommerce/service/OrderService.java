package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.DuplicatedResourceException;
import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.Order;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.service.repository.OrderRepository;
import com.stellaTech.ecommerce.service.specification.OrderSpecs;
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
    public Order createOrder(Long productId, Long platformUserId, int productCount) throws DuplicatedResourceException, InvalidInputException {
        if (orderExists(productId, platformUserId)) {
            throw new DuplicatedResourceException("Order has already been created");
        }
        Product product = productService.getProductById(productId);
        PlatformUser platformUser = platformUserService.getUserById(platformUserId);
        Order newOrder = new Order(product, platformUser, productCount);
        return orderRepository.save(newOrder);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllActiveOrders() {
        return orderRepository.findAll(OrderSpecs.isNotDeleted());
    }

    @Transactional(readOnly = true)
    public boolean orderExists(Long productId, Long platformUserId) {
        return orderRepository.exists(
                OrderSpecs.hasNotBeenDeleted(productId, platformUserId)
        );
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
