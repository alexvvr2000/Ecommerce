package com.stellaTech.ecommerce.service.order;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrderItem;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import com.stellaTech.ecommerce.service.dto.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.platformUser.PlatformUserService;
import com.stellaTech.ecommerce.service.product.ProductService;
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
    public OrderDto<OrderDto.OrderItemSelectDto> createOrder(
            @Validated(NullCheckGroup.OnInsert.class) OrderDto<OrderDto.OrderItemInsertDto> dto
    ) throws ResourceNotFoundException {
        PlatformUser persistedUser = platformUserService.getUserById(dto.getPlatformUserId());
        CustomerOrder.CustomerOrderBuilder customerOrderBuilder = CustomerOrder.builder()
                .platformUser(persistedUser);
        for (OrderDto.OrderItemDto currentItemDto : dto.getOrderItems()) {
            Product persistedProduct = productService.getProductById(currentItemDto.getProductId());
            CustomerOrderItem newCustomerOrderItem = CustomerOrderItem.builder()
                    .product(persistedProduct)
                    .quantity(currentItemDto.getQuantity())
                    .build();
            customerOrderBuilder.customerOrderItem(newCustomerOrderItem);
        }
        CustomerOrder newCustomerOrderInstance = customerOrderBuilder.build();
        orderRepository.save(newCustomerOrderInstance);
        return orderSummary(newCustomerOrderInstance);
    }

    protected OrderDto<OrderDto.OrderItemSelectDto> orderSummary(
            CustomerOrder customerOrder
    ) {
        OrderDto.OrderDtoBuilder<OrderDto.OrderItemSelectDto> orderDtoBuilder = OrderDto
                .<OrderDto.OrderItemSelectDto>builder()
                .id(customerOrder.getId())
                .platformUserId(customerOrder.getPlatformUser().getId())
                .totalAmount(customerOrder.getTotalAmount());
        for (CustomerOrderItem customerOrderItem : customerOrder.getCustomerOrderItems()) {
            OrderDto.OrderItemSelectDto orderItemSelectDto = OrderDto.OrderItemSelectDto.builder()
                    .orderItemId(customerOrderItem.getId())
                    .orderId(customerOrderItem.getCustomerOrder().getId())
                    .productId(customerOrderItem.getProduct().getId())
                    .quantity(customerOrderItem.getQuantity())
                    .price(customerOrderItem.getProductPriceSnapshot().getPrice())
                    .subtotal(customerOrderItem.getSubtotal())
                    .build();
            orderDtoBuilder.orderItem(orderItemSelectDto);
        }
        return orderDtoBuilder.build();
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
                new ResourceNotFoundException("Active order with id " + id + " was not found")
        );
    }

    public OrderDto<OrderDto.OrderItemSelectDto> getOrderDtoById(Long id) {
        CustomerOrder persistedCustomerOrder = getOrderById(id);
        return orderSummary(persistedCustomerOrder);
    }
}
