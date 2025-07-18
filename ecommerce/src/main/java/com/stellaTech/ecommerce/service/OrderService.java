package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.model.OrderManagement.OrderItem;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.ProductManagement.Product;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import com.stellaTech.ecommerce.service.dataDto.OrderDto;
import com.stellaTech.ecommerce.service.serviceDto.IdDtoResponse;
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
    public IdDtoResponse<OrderDto> createOrder(@Valid OrderDto dto) throws ResourceNotFoundException {
        PlatformUser persistedUser = platformUserService.getUserById(dto.getPlatformUserId());
        Order newOrder = new Order(persistedUser);
        for (OrderDto.OrderItemDto currentItemDto : dto.getOrderItems()) {
            Product persistedProduct = productService.getProductById(currentItemDto.getProductId());
            OrderItem newOrderItem = new OrderItem(persistedProduct, currentItemDto.getQuantity());
            newOrder.addOrderItem(newOrderItem);
        }
        orderRepository.save(newOrder);
        return new IdDtoResponse<>(newOrder.getId(), orderSummary(newOrder).getDto());
    }

    private IdDtoResponse<OrderDto> orderSummary(Order order) {
        OrderDto orderDto = new OrderDto();
        Long orderId = order.getId();
        orderDto.setPlatformUserId(orderId);
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderDto.OrderSelectDto orderSelectDto = new OrderDto.OrderSelectDto(
                    orderItem.getId(),
                    orderItem.getOrder().getId(),
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity(),
                    orderItem.getProductPriceSnapshot().getPrice(),
                    orderItem.getSubtotal()
            );
            orderDto.addItem(orderSelectDto);
        }
        return new IdDtoResponse<>(orderId, orderDto);
    }

    @Transactional(readOnly = true)
    public List<IdDtoResponse<OrderDto>> getAllOrders() {
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

    public OrderDto getOrderDtoById(Long id) {
        Order persistedOrder = getOrderById(id);
        return orderSummary(persistedOrder).getDto();
    }
}
