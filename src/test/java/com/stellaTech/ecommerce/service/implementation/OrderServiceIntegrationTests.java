package com.stellaTech.ecommerce.service.implementation;

import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.generics.OrderService;
import com.stellaTech.ecommerce.service.generics.PlatformUserService;
import com.stellaTech.ecommerce.service.generics.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderServiceIntegrationTests {
    protected final DataGenerationService.NumberRange orderItemAmountRange = DataGenerationService.NumberRange.builder()
            .minAmount(1)
            .maxAmount(10)
            .build();
    protected final Long testOrderId = 1L;
    protected final Long testUserId = 1L;
    protected final int amountListProductDto = 7;
    @Autowired
    protected DataGenerationService dataGenerationService;
    @Autowired
    protected OrderService orderService;
    @Autowired
    protected PlatformUserService platformUserService;
    @Autowired
    protected ProductService productService;

    private OrderDto<OrderDto.OrderItemSelectDto> createPersistedOrder(long userId, Long orderId, DataGenerationService.NumberRange productListSizeRange) {
        List<ProductDto> productDtoList = dataGenerationService.createValidListProductDto(
                amountListProductDto, productListSizeRange
        );
        OrderDto<OrderDto.OrderItemInsertDto> newOrder = dataGenerationService.createValidOrderDto(
                productDtoList.stream().map(
                        productDto -> productService.createProduct(productDto)
                ).toList(),
                orderItemAmountRange, userId, orderId
        );
        return orderService.createOrder(newOrder);
    }

    @Test
    void createOrderWithValidData() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto persistedUser = platformUserService.createUser(userDto);
        OrderDto<OrderDto.OrderItemSelectDto> newOrder = createPersistedOrder(
                persistedUser.getId(),
                testOrderId,
                orderItemAmountRange
        );
        OrderDto<OrderDto.OrderItemSelectDto> savedOrder = orderService.getOrderDtoById(
                newOrder.getId()
        );
        BigDecimal expectedPrice = savedOrder.getOrderItems()
                .stream().map(currentItem -> {
                    BigDecimal storedPrice = currentItem.getPrice();
                    Long productId = currentItem.getProductId();
                    Integer storedAmount = currentItem.getQuantity();
                    ProductDto storedProduct = productService.getProductDtoById(productId);
                    assertEquals(storedProduct.getPrice(), storedPrice);
                    assertEquals(productId, storedProduct.getId());
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
    void getOrderDtoByIdWhenOrderExists() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto persistedUser = platformUserService.createUser(userDto);
        OrderDto<OrderDto.OrderItemSelectDto> savedOrder = createPersistedOrder(
                persistedUser.getId(),
                testOrderId,
                orderItemAmountRange
        );
        assertNotNull(savedOrder);
        assertEquals(
                orderService.getOrderDtoById(savedOrder.getId()),
                savedOrder
        );
    }

    @Test
    void logicallyDeleteByIdWhenOrderExists() {
        PlatformUserDto userDto = dataGenerationService.createValidPlatformUserDto(testUserId);
        PlatformUserDto persistedUser = platformUserService.createUser(userDto);
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrderToDelete = createPersistedOrder(
                persistedUser.getId(),
                testOrderId,
                orderItemAmountRange
        );
        Long persistedOrderId = persistedOrderToDelete.getId();
        assertEquals(orderService.getOrderDtoById(persistedOrderId), persistedOrderToDelete);

        orderService.logicallyDeleteById(persistedOrderToDelete.getId());
        assertThrows(ResourceNotFoundException.class, () -> orderService.logicallyDeleteById(persistedOrderId));
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderDtoById(persistedOrderId));
    }
}
