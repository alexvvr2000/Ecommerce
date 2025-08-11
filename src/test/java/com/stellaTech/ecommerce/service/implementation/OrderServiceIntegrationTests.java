package com.stellaTech.ecommerce.service.implementation;

import com.github.javafaker.Faker;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderServiceIntegrationTests {
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
    @Autowired
    protected PlatformUserService platformUserService;

    private List<ProductDto> generateRandomProductList(int itemAmount) {
        List<ProductDto> list = new ArrayList<>();
        for (int i = 0; i < itemAmount; i += 1) {
            ProductDto newProduct = productService.createProduct(
                    dataGenerationService.createInsertProductDto()
            );
            list.add(
                    newProduct
            );
        }
        return list;
    }

    private OrderDto<OrderDto.OrderItemSelectDto> createPersistedOrder() {
        PlatformUserDto randomUser = platformUserService.createUser(
                dataGenerationService.createInsertUserDto()
        );
        OrderDto<OrderDto.OrderItemInsertDto> newOrder = dataGenerationService.createInsertDtoOrder(
                generateRandomProductList(randomItemAmount), orderItemAmountRange, randomUser
        );
        return orderService.createOrder(newOrder);
    }

    @Test
    void createOrderWithValidData() {
        OrderDto<OrderDto.OrderItemSelectDto> newOrder = createPersistedOrder();
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
        OrderDto<OrderDto.OrderItemSelectDto> savedOrder = createPersistedOrder();
        assertNotNull(savedOrder);
        assertEquals(
                orderService.getOrderDtoById(savedOrder.getId()),
                savedOrder
        );
    }

    @Test
    void deleteOrderFromDatabase() {
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrderToDelete = createPersistedOrder();
        Long persistedOrderId = persistedOrderToDelete.getId();
        assertEquals(orderService.getOrderDtoById(persistedOrderId), persistedOrderToDelete);

        for (int i = 0; i < orderCreationLimit; i += 1) {
            createPersistedOrder();
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
