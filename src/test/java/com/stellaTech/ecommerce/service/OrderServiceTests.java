package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.DataGenerationTesting;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTests extends DataGenerationTesting {
    private final int MAXIMUM_ITEM_AMOUNT = 10;
    private final int MINIMAL_ITEM_AMOUNT = 1;
    private final int MINIMAL_ORDER_ITEM_AMOUNT = 1;
    private final int MAXIMUM_ORDER_ITEM_AMOUNT = 10;
    private final OrderItemAmountRange orderItemAmountRange = OrderItemAmountRange.builder()
            .minOrderItemAmount(MINIMAL_ORDER_ITEM_AMOUNT)
            .maxOrderItemAmount(MAXIMUM_ORDER_ITEM_AMOUNT)
            .build();

    @Test
    void createOrder_WithValidData() {
        PlatformUserDto randomUser = createPersistedUser();
        int randomItemAmount = faker.number().numberBetween(MINIMAL_ITEM_AMOUNT, MAXIMUM_ITEM_AMOUNT);
        OrderDto<OrderDto.OrderItemInsertDto> newOrder = createInsertDtoOrder(
                randomItemAmount, randomUser, orderItemAmountRange
        );
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrder = orderService.createOrder(newOrder);
        OrderDto<OrderDto.OrderItemSelectDto> savedOrder = orderService.getOrderDtoById(
                persistedOrder.getId()
        );
        BigDecimal expectedPrice = savedOrder.getOrderItems()
                .stream().map(currentItem -> {
                    BigDecimal storedPrice = currentItem.getPrice();
                    Long productId = currentItem.getProductId();
                    ProductDto storedProduct = productService.getProductDtoById(productId);
                    assertEquals(storedProduct.getPrice(), storedPrice);
                    assertEquals(productId, storedProduct.getId());
                    Integer storedAmount = currentItem.getQuantity();
                    return storedPrice.multiply(BigDecimal.valueOf(storedAmount));
                }).reduce(BigDecimal::add)
                .orElseThrow(
                        () -> new IllegalArgumentException("The item price snapshots are not saved correctly")
                );
        assertNotNull(savedOrder);
        assertEquals(savedOrder.getId(), newOrder.getPlatformUserId());
        assertEquals(savedOrder.getTotalAmount(), expectedPrice);
    }

    @Test
    void getOrderDtoById_WhenOrderExists() {
        PlatformUserDto randomUser = createPersistedUser();
        int randomItemAmount = faker.number().numberBetween(MINIMAL_ITEM_AMOUNT, MAXIMUM_ITEM_AMOUNT);
        OrderDto<OrderDto.OrderItemInsertDto> validOrder = createInsertDtoOrder(
                randomItemAmount, randomUser, orderItemAmountRange
        );
        OrderDto<OrderDto.OrderItemSelectDto> savedOrder = orderService.createOrder(validOrder);
        assertNotNull(savedOrder);
        assertEquals(
                orderService.getOrderDtoById(savedOrder.getId()),
                savedOrder
        );
    }

    @Test
    void deleteOrderFromDatabase() {
        PlatformUserDto randomUser = createPersistedUser();
        int randomItemAmount = faker.number().numberBetween(MINIMAL_ITEM_AMOUNT, MAXIMUM_ITEM_AMOUNT);
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrder = createPersistedOrder(
                randomItemAmount, randomUser, orderItemAmountRange
        );
        Long orderId = persistedOrder.getId();
        orderService.logicallyDeleteOrder(persistedOrder.getId());
        assertThrows(ResourceNotFoundException.class, () -> orderService.logicallyDeleteOrder(orderId));
    }
}
