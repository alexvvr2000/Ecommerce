package com.stellaTech.ecommerce;

import com.github.javafaker.Faker;
import com.stellaTech.ecommerce.service.order.OrderService;
import com.stellaTech.ecommerce.service.platformUser.PlatformUserService;
import com.stellaTech.ecommerce.service.product.ProductService;
import com.stellaTech.ecommerce.service.dto.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.PlatformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@Rollback
public class DataGenerationTesting {
    protected static final Faker faker = new Faker(new Locale("es-MX"));

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected PlatformUserService platformUserService;

    @Autowired
    protected ProductService productService;

    protected @Validated(NullCheckGroup.OnInsert.class) PlatformUserDto createInsertUserDto() {
        return PlatformUserDto.builder()
                .curp(faker.regexify("[A-Z]{4}[0-9]{6}[A-Z]{6}[0-9]{2}"))
                .fullName(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phoneNumber(faker.phoneNumber().cellPhone())
                .password(faker.internet().password(8, 16))
                .build();
    }

    protected @Validated(NullCheckGroup.OnInsert.class) ProductDto createInsertProductDto() {
        return ProductDto.builder()
                .name(faker.commerce().productName())
                .mdFormatDescription("**%.100s**".formatted(faker.lorem().paragraph()))
                .mainImageUrl(faker.internet().image())
                .price(new BigDecimal(faker.commerce().price().replace(",", ".")))
                .build();
    }

    protected @Valid OrderDto.OrderItemInsertDto createInsertDtoOrderItem(
            @Validated(NullCheckGroup.OnRead.class) ProductDto product, int itemAmount
    ) {
        return OrderDto.OrderItemInsertDto.builder()
                .productId(product.getId())
                .quantity(itemAmount)
                .build();
    }

    protected @Validated(NullCheckGroup.OnRead.class) ProductDto createPersistedProduct() {
        ProductDto newProduct = createInsertProductDto();
        return productService.createProduct(newProduct);
    }

    protected @Validated(NullCheckGroup.OnRead.class) PlatformUserDto createPersistedUser() {
        PlatformUserDto newUser = createInsertUserDto();
        return platformUserService.createUser(newUser);
    }

    protected @Valid List<OrderDto.OrderItemInsertDto> createInsertDtoOrderItemList(
            int itemAmount, OrderItemAmountRange itemAmountRange
    ) {
        List<OrderDto.OrderItemInsertDto> orderItems = new ArrayList<>();
        for (int i = 0; i < itemAmount; i += 1) {
            ProductDto newProduct = createPersistedProduct();
            int orderItemAmount = faker.number().numberBetween(
                    itemAmountRange.minOrderItemAmount, itemAmountRange.maxOrderItemAmount
            );
            orderItems.add(
                    createInsertDtoOrderItem(newProduct, orderItemAmount)
            );
        }
        return orderItems;
    }

    protected @Valid OrderDto<OrderDto.OrderItemInsertDto> createInsertDtoOrder(
            int itemAmount,
            @Validated(NullCheckGroup.OnRead.class) PlatformUserDto user,
            OrderItemAmountRange itemAmountRange
    ) {
        List<OrderDto.OrderItemInsertDto> productList = createInsertDtoOrderItemList(
                itemAmount, itemAmountRange
        );
        return OrderDto
                .<OrderDto.OrderItemInsertDto>builder()
                .platformUserId(user.getId())
                .orderItems(productList)
                .build();
    }

    protected @Valid OrderDto<OrderDto.OrderItemSelectDto> createPersistedOrder(
            int itemAmount,
            @Validated(NullCheckGroup.OnRead.class) PlatformUserDto user,
            OrderItemAmountRange itemAmountRange
    ) {
        OrderDto<OrderDto.OrderItemInsertDto> newOrder = createInsertDtoOrder(
                itemAmount, user, itemAmountRange
        );
        return orderService.createOrder(newOrder);
    }

    @Value
    @Builder
    public static class OrderItemAmountRange {
        int minOrderItemAmount;
        int maxOrderItemAmount;
    }
}
