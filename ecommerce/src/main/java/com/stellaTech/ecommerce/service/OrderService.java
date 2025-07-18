package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.model.OrderManagement.OrderItem;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.ProductManagement.Product;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import com.stellaTech.ecommerce.service.dataDto.OrderDto;
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
    private PlatformUserService platformUserService;

    @Autowired
    private ProductService productService;

    @Transactional
    public void logicallyDeleteOrder(Long id) throws ResourceNotFoundException {
        Order order = getOrderById(id);
        order.setDeleted(true);
    }

    @Transactional
    public OrderDto<OrderDto.OrderItemSelectDto> createOrder(@Valid OrderDto<OrderDto.OrderItemInsertDto> dto) throws ResourceNotFoundException {
        PlatformUser persistedUser = platformUserService.getUserById(dto.getPlatformUserId());
        Order newOrder = new Order(persistedUser);
        for (OrderDto.OrderItemDto currentItemDto : dto.getOrderItems()) {
            Product persistedProduct = productService.getProductById(currentItemDto.getProductId());
            OrderItem newOrderItem = new OrderItem(persistedProduct, currentItemDto.getQuantity());
            newOrder.addOrderItem(newOrderItem);
        }
        orderRepository.save(newOrder);
        return orderSummary(newOrder);
    }

    private OrderDto<OrderDto.OrderItemSelectDto> orderSummary(Order order) {
        OrderDto<OrderDto.OrderItemSelectDto> orderDto = new OrderDto<>();
        Long orderId = order.getId();
        orderDto.setPlatformUserId(orderId);
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderDto.OrderItemSelectDto orderItemSelectDto = new OrderDto.OrderItemSelectDto(
                    orderItem.getId(),
                    orderItem.getOrder().getId(),
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity(),
                    orderItem.getProductPriceSnapshot().getPrice(),
                    orderItem.getSubtotal()
            );
            orderDto.addItem(orderItemSelectDto);
        }
        return orderDto;
    }

    @Transactional(readOnly = true)
    public List<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders() {
        return orderRepository.findAll(
                OrderSpecs.isNotDeleted()
        ).stream().map(this::orderSummary).toList();
    }

    @Transactional(readOnly = true)
    protected Order getOrderById(Long id) throws ResourceNotFoundException {
        return orderRepository.findOne(
                OrderSpecs.hasNotBeenDeleted(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }

    public OrderDto<OrderDto.OrderItemSelectDto> getOrderDtoById(Long id) {
        Order persistedOrder = getOrderById(id);
        return orderSummary(persistedOrder);
    }
}
