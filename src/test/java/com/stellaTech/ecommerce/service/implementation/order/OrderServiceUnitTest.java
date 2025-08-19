package com.stellaTech.ecommerce.service.implementation.order;

import com.stellaTech.ecommerce.DataGenerationService;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.ProductRepository;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.implementation.OrderServiceImp;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
    protected final DataGenerationService.NumberRange orderItemQuantityRange =
            DataGenerationService.NumberRange.builder()
                    .minAmount(1)
                    .maxAmount(10)
                    .build();
    protected final DataGenerationService.NumberRange orderItemCreationRange =
            DataGenerationService.NumberRange.builder()
                    .minAmount(1)
                    .maxAmount(10)
                    .build();
    protected final DataGenerationService.NumberRange idCreationRange =
            DataGenerationService.NumberRange.builder()
                    .minAmount(1)
                    .maxAmount(10)
                    .build();
    protected final Long testOrderId = 1L;
    protected final Long testUserId = 1L;
    protected final int orderItemListSize = 7;
    protected final DataGenerationService dataGenerationService = new DataGenerationService();
    @Mock
    protected ProductRepository productRepository;
    @Mock
    protected PlatformUserRepository userRepository;
    @Mock
    protected OrderRepository orderRepository;
    @InjectMocks
    protected OrderServiceImp orderService;

    protected boolean persistedItemsAreEqual(
            @Valid List<ProductDto> productDtoList,
            @Valid OrderDto<OrderDto.OrderItemSelectDto> validOrderDto
    ) {
        List<Long> persistedProductIds = validOrderDto.getOrderItems().stream()
                .map(OrderDto.OrderItemSelectDto::getProductId)
                .sorted()
                .toList();
        List<Long> insertedProductIds = productDtoList.stream()
                .map(ProductDto::getId)
                .sorted()
                .toList();

        assertEquals(persistedProductIds, insertedProductIds);

        BigDecimal totalPrice = validOrderDto.getOrderItems().stream()
                .map(item -> {
                    ProductDto originalProduct = productDtoList.stream()
                            .filter(product -> Objects.equals(product.getId(), item.getProductId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Product not found for ID: " + item.getProductId()));
                    assertEquals(originalProduct.getPrice(), item.getPrice());
                    BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    assertEquals(subtotal, item.getSubtotal());
                    return subtotal;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalPrice.equals(validOrderDto.getTotalAmount());
    }

    @Test
    void createOrderWithValidData() throws NoSuchFieldException, IllegalAccessException {
        List<ProductDto> productDtoList = dataGenerationService.createValidListProductDto(
                orderItemListSize, idCreationRange
        );
        productDtoList.forEach(
                currentDto -> {
                    try {
                        when(productRepository.getProductById(currentDto.getId()))
                                .thenReturn(dataGenerationService.createProductModel(currentDto));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        PlatformUserDto userData = dataGenerationService.createValidPlatformUserDto(testUserId);
        when(userRepository.getUserById(testUserId)).thenReturn(
                dataGenerationService.createPlatformUserModel(userData)
        );

        OrderDto<OrderDto.OrderItemInsertDto> validOrderDto = dataGenerationService.createValidOrderDto(
                productDtoList, orderItemQuantityRange, userData.getId(), testOrderId
        );
        OrderDto<OrderDto.OrderItemSelectDto> result = orderService.createOrder(validOrderDto);

        assertNotNull(result);
        assertSame(result.getPlatformUserId(), userData.getId());
        result.getOrderItems().forEach(
                item -> assertEquals(item.getOrderId(), result.getId())
        );
        assertTrue(persistedItemsAreEqual(productDtoList, result));
    }

    @Test
    void getOrderIdWhenExistsInDatabase() throws NoSuchFieldException, IllegalAccessException {
        List<ProductDto> productDtoList = dataGenerationService.createValidListProductDto(
                orderItemListSize, idCreationRange
        );
        OrderDto<OrderDto.OrderItemInsertDto> validOrderDto = dataGenerationService.createValidOrderDto(
                productDtoList, orderItemQuantityRange, testUserId, testOrderId
        );

        CustomerOrder mockOrder = dataGenerationService.createCustomerOrderModel(validOrderDto, productDtoList);
        when(orderRepository.getOrderById(testOrderId)).thenReturn(mockOrder);

        OrderDto<OrderDto.OrderItemSelectDto> result = orderService.getOrderDtoById(testOrderId);
        assertNotNull(result);
        assertSame(result.getPlatformUserId(), validOrderDto.getId());
        result.getOrderItems().forEach(
                item -> assertEquals(item.getOrderId(), result.getId())
        );
        assertTrue(persistedItemsAreEqual(productDtoList, result));
    }

    @Test
    void logicallyDeleteByIdWhenOrderExists() throws NoSuchFieldException, IllegalAccessException {
        OrderDto<OrderDto.OrderItemInsertDto> newOrder = dataGenerationService.createValidOrderDto(
                dataGenerationService.createValidListProductDto(orderItemListSize, idCreationRange),
                orderItemCreationRange,
                testUserId,
                testOrderId
        );

        CustomerOrder mockOrder = dataGenerationService.createCustomerOrderModel(newOrder);
        when(orderRepository.getOrderById(testOrderId)).thenReturn(mockOrder);

        assertFalse(mockOrder.isDeleted());
        orderService.logicallyDeleteById(testOrderId);
        assertTrue(mockOrder.isDeleted());
    }
}