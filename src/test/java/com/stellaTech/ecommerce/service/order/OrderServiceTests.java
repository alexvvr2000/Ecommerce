package com.stellaTech.ecommerce.service.order;

import com.github.javafaker.Faker;
import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@Rollback
public class OrderServiceTests {
    protected static final Faker faker = new Faker(new Locale("es-MX"));
    private final DataGenerationService.numberRange productCreationRange = DataGenerationService.numberRange.builder()
            .minAmount(1)
            .maxAmount(10)
            .build();
    private final DataGenerationService.numberRange orderItemAmountRange = DataGenerationService.numberRange.builder()
            .minAmount(1)
            .maxAmount(10)
            .build();
    private final int randomItemAmount = faker.number().numberBetween(
            productCreationRange.getMinAmount(), productCreationRange.getMaxAmount()
    );
    @Autowired
    private DataGenerationService dataGenerationService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @Test
    void createOrder_WithValidData() {
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
    void getOrderDtoById_WhenOrderExists() {
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
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrder = dataGenerationService.createPersistedOrder(
                randomItemAmount, randomUser, orderItemAmountRange
        );
        Long orderId = persistedOrder.getId();
        orderService.logicallyDeleteOrder(persistedOrder.getId());
        assertThrows(ResourceNotFoundException.class, () -> orderService.logicallyDeleteOrder(orderId));
    }
}
