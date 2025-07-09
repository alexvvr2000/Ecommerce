package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.model.OrderManagement.OrderItem;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.service.repository.OrderRepository;
import com.stellaTech.ecommerce.service.specification.OrderSpecs;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public Order createOrder(OrderDTO newOrder) throws InvalidInputException, ResourceNotFoundException {
        PlatformUser platformUser = platformUserService.getUserById(newOrder.platformUserId);
        Set<OrderItem> orderItems = new HashSet<>();
        newOrder.items.forEach((currentItem) -> {
            Product productOrder = productService.getProductById(currentItem.productId);
            OrderItem newOrderItem = new OrderItem(productOrder, currentItem.productCount);
            orderItems.add(newOrderItem);
        });
        Order newPersistentOrder = new Order(orderItems, platformUser);
        return orderRepository.save(newPersistentOrder);
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
    public static class OrderDTO {
        Long platformUserId;
        Set<OrderItemDTO> items;

        @Value
        public static class OrderItemDTO {
            Long productId;
            int productCount;

            @Override
            public int hashCode() {
                return Objects.hash(productId);
            }
        }
    }
}
