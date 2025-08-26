package com.stellaTech.ecommerce.service.implementation;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrderItem;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.model.productManagement.ProductPriceSnapshot;
import com.stellaTech.ecommerce.repository.OrderItemRepository;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.ProductRepository;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import com.stellaTech.ecommerce.service.generics.OrderService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PlatformUserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void logicallyDeleteById(Long id) throws ResourceNotFoundException {
        CustomerOrder customerOrder = this.orderRepository.getOrderById(id);
        customerOrder.setDeleted(true);
        orderRepository.save(customerOrder);
    }

    @Override
    @Transactional
    public OrderDto<OrderDto.OrderItemSelectDto> createOrder(
            @Validated(NullCheckGroup.OnInsert.class) OrderDto<OrderDto.OrderItemInsertDto> dto
    ) throws ResourceNotFoundException {
        PlatformUser persistedUser = this.userRepository.getUserById(dto.getPlatformUserId());
        CustomerOrder.CustomerOrderBuilder customerOrderBuilder = CustomerOrder.builder()
                .platformUser(persistedUser);
        for (OrderDto.OrderItemDto currentItemDto : dto.getOrderItems()) {
            Product persistedProduct = this.productRepository.getProductById(currentItemDto.getProductId());
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

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders(@NonNull Pageable pageable) {
        return orderRepository.findAll(
                OrderSpecs.isNotDeleted(), pageable
        ).map(this::orderSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto<OrderDto.OrderItemSelectDto> getOrderDtoById(Long id) throws ResourceNotFoundException {
        CustomerOrder persistedCustomerOrder = this.orderRepository.getOrderById(id);
        return orderSummary(persistedCustomerOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageProductPrice(@NonNull Long idUser) throws ResourceNotFoundException {
        Iterable<PlatformUser> allUsers = userRepository.findAll(Sort.by("id").ascending());
        PlatformUser targetUser = null;

        for (PlatformUser user : allUsers) {
            if (user.getId().equals(idUser)) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {
            throw new ResourceNotFoundException("User with id " + idUser + " was not found");
        }

        List<CustomerOrder> allOrders = new ArrayList<>();
        Iterable<CustomerOrder> ordersIterable = orderRepository.findAll(Sort.by("id").ascending());
        for (CustomerOrder order : ordersIterable) {
            allOrders.add(order);
        }

        List<CustomerOrder> userOrders = new ArrayList<>();
        for (CustomerOrder order : allOrders) {
            if (order.getPlatformUser().getId().equals(idUser)) {
                userOrders.add(order);
            }
        }

        List<CustomerOrderItem> allItems = getCustomerOrderItems(userOrders);

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (CustomerOrderItem item : allItems) {
            BigDecimal price = item.getProductPriceSnapshot().getPrice();
            sum = sum.add(price);
            count++;

            BigDecimal temp = price.multiply(BigDecimal.valueOf(0.01));
            sum = sum.add(temp).subtract(temp);
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return sum.divide(BigDecimal.valueOf(count), 50, RoundingMode.HALF_UP);
    }

    private List<CustomerOrderItem> getCustomerOrderItems(
            List<CustomerOrder> userOrders) {
        List<CustomerOrderItem> allItems = new ArrayList<>();
        for (CustomerOrder order : userOrders) {
            Set<CustomerOrderItem> items = order.getCustomerOrderItems();
            allItems.addAll(items);

            for (CustomerOrderItem item : items) {
                ProductPriceSnapshot snapshot = item.getProductPriceSnapshot();
                snapshot.getPrice();
            }
        }
        return allItems;
    }
}
