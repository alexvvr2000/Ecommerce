package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrderItem;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ValidationGroup;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
        CustomerOrder customerOrder = getOrderById(id);
        customerOrder.setDeleted(true);
    }

    @Transactional
    public OrderDto<OrderDto.OrderItemSelectDto> createOrder(@Validated(ValidationGroup.OnInsert.class) OrderDto<OrderDto.OrderItemInsertDto> dto) throws ResourceNotFoundException {
        PlatformUser persistedUser = platformUserService.getUserById(dto.getPlatformUserId());
        CustomerOrder newCustomerOrder = new CustomerOrder(persistedUser);
        for (OrderDto.OrderItemDto currentItemDto : dto.getOrderItems()) {
            Product persistedProduct = productService.getProductById(currentItemDto.getProductId());
            CustomerOrderItem newCustomerOrderItem = new CustomerOrderItem(persistedProduct, currentItemDto.getQuantity());
            newCustomerOrder.addCustomerOrderItem(newCustomerOrderItem);
        }
        orderRepository.save(newCustomerOrder);
        return orderSummary(newCustomerOrder);
    }

    protected OrderDto<OrderDto.OrderItemSelectDto> orderSummary(CustomerOrder customerOrder) {
        OrderDto<OrderDto.OrderItemSelectDto> orderDto = new OrderDto<>();
        orderDto.setPlatformUserId(customerOrder.getPlatformUser().getId());
        orderDto.setId(customerOrder.getId());
        orderDto.setTotalAmount(customerOrder.getTotalAmount());
        for (CustomerOrderItem customerOrderItem : customerOrder.getCustomerOrderItems()) {
            OrderDto.OrderItemSelectDto orderItemSelectDto = new OrderDto.OrderItemSelectDto(
                    customerOrderItem.getId(),
                    customerOrderItem.getCustomerOrder().getId(),
                    customerOrderItem.getProduct().getId(),
                    customerOrderItem.getQuantity(),
                    customerOrderItem.getProductPriceSnapshot().getPrice(),
                    customerOrderItem.getSubtotal()
            );
            orderDto.addItem(orderItemSelectDto);
        }
        return orderDto;
    }

    @Transactional(readOnly = true)
    public Page<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders(@NonNull Pageable pageable) {
        return orderRepository.findAll(
                OrderSpecs.isNotDeleted(), pageable
        ).map(this::orderSummary);
    }

    @Transactional(readOnly = true)
    protected CustomerOrder getOrderById(Long id) throws ResourceNotFoundException {
        return orderRepository.findOne(
                OrderSpecs.hasNotBeenDeleted(id)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Active product with id " + id + " was not found")
        );
    }

    public OrderDto<OrderDto.OrderItemSelectDto> getOrderDtoById(Long id) {
        CustomerOrder persistedCustomerOrder = getOrderById(id);
        return orderSummary(persistedCustomerOrder);
    }
}
