package com.stellaTech.ecommerce.service.implementation;

import com.github.javafaker.Faker;
import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.generics.OrderService;
import com.stellaTech.ecommerce.service.generics.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class OrderServiceTests {
    protected final Faker faker = new Faker(new Locale("es-MX"));
    protected final DataGenerationService.numberRange productCreationRange = DataGenerationService.numberRange.builder()
            .minAmount(1)
            .maxAmount(10)
            .build();
    protected final DataGenerationService.numberRange orderItemAmountRange = DataGenerationService.numberRange.builder()
            .minAmount(1)
            .maxAmount(10)
            .build();
    protected final int orderCreationLimit = 10;
    protected final int randomItemAmount = faker.number().numberBetween(
            productCreationRange.getMinAmount(), productCreationRange.getMaxAmount()
    );
    protected final Pageable defaultPageableObject = Pageable.ofSize(orderCreationLimit);
    @Autowired
    protected DataGenerationService dataGenerationService;
    @Autowired
    protected OrderService orderService;
    @Autowired
    protected ProductService productService;

    @Test
    void createOrderWithValidData() {
        PlatformUserDto randomUser = dataGenerationService.createPersistedUser();
        OrderDto<OrderDto.OrderItemInsertDto> newOrder = dataGenerationService.createInsertDtoOrder(
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
        PlatformUserDto randomUser = dataGenerationService.createPersistedUser();
        OrderDto<OrderDto.OrderItemInsertDto> validOrder = dataGenerationService.createInsertDtoOrder(
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
        PlatformUserDto randomUser = dataGenerationService.createPersistedUser();
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrderToDelete = dataGenerationService.createPersistedOrder(
                randomItemAmount, randomUser, orderItemAmountRange
        );
        Long persistedOrderId = persistedOrderToDelete.getId();
        assertEquals(orderService.getOrderDtoById(randomUser.getId()), persistedOrderToDelete);

        for (int i = 0; i < orderCreationLimit; i += 1) {
            PlatformUserDto randomPageUser = dataGenerationService.createPersistedUser();
            dataGenerationService.createPersistedOrder(
                    randomItemAmount, randomPageUser, orderItemAmountRange
            );
        }

        orderService.logicallyDeleteById(persistedOrderToDelete.getId());
        assertThrows(ResourceNotFoundException.class, () -> orderService.logicallyDeleteById(persistedOrderId));
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderDtoById(persistedOrderId));

        Page<OrderDto<OrderDto.OrderItemSelectDto>> orderSelectionPage = orderService.getAllOrders(
                defaultPageableObject
        );
        boolean deletedOrderExistsInPage = orderSelectionPage.getContent().contains(persistedOrderToDelete);
        assertFalse(deletedOrderExistsInPage);
    }
}
