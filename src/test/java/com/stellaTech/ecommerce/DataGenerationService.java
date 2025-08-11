package com.stellaTech.ecommerce;

import com.github.javafaker.Faker;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import com.stellaTech.ecommerce.service.generics.OrderService;
import com.stellaTech.ecommerce.service.generics.PlatformUserService;
import com.stellaTech.ecommerce.service.generics.ProductService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Transactional
@Rollback
@Service
public class DataGenerationService {
    protected final Faker faker = new Faker(new Locale("es-MX"));

    @Autowired
    protected OrderService orderServiceImp;

    @Autowired
    protected PlatformUserService platformUserServiceImp;

    @Autowired
    protected ProductService productServiceImp;

    public @Validated(NullCheckGroup.OnInsert.class) PlatformUserDto createInsertUserDto() {
        return PlatformUserDto.builder()
                .curp(faker.regexify("[A-Z]{4}[0-9]{6}[A-Z]{6}[0-9]{2}"))
                .fullName(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phoneNumber(faker.phoneNumber().cellPhone())
                .password(faker.internet().password(8, 16))
                .build();
    }

    public @Validated(NullCheckGroup.OnRead.class) PlatformUserDto createPersistedUser() {
        PlatformUserDto newUser = createInsertUserDto();
        return platformUserServiceImp.createUser(newUser);
    }

    public @Validated(NullCheckGroup.OnInsert.class) ProductDto createInsertProductDto() {
        return ProductDto.builder()
                .name(faker.commerce().productName())
                .mdFormatDescription("**%.100s**".formatted(faker.lorem().paragraph()))
                .mainImageUrl(faker.internet().image())
                .price(new BigDecimal(faker.commerce().price().replace(",", ".")))
                .build();
    }

    public @Validated(NullCheckGroup.OnRead.class) ProductDto createPersistedProduct() {
        ProductDto newProduct = createInsertProductDto();
        return productServiceImp.createProduct(newProduct);
    }

    public @Valid OrderDto.OrderItemInsertDto createInsertDtoOrderItem(
            @Validated(NullCheckGroup.OnRead.class) ProductDto product, int itemAmount
    ) {
        return OrderDto.OrderItemInsertDto.builder()
                .productId(product.getId())
                .quantity(itemAmount)
                .build();
    }

    public @Valid OrderDto<OrderDto.OrderItemInsertDto> createInsertDtoOrder(
            int itemAmount,
            @Validated(NullCheckGroup.OnRead.class) PlatformUserDto user,
            numberRange itemAmountRange
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

    public @Valid List<OrderDto.OrderItemInsertDto> createInsertDtoOrderItemList(
            int itemAmount, numberRange itemAmountRange
    ) {
        List<OrderDto.OrderItemInsertDto> orderItems = new ArrayList<>();
        for (int i = 0; i < itemAmount; i += 1) {
            ProductDto newProduct = createPersistedProduct();
            int orderItemAmount = faker.number().numberBetween(
                    itemAmountRange.minAmount, itemAmountRange.maxAmount
            );
            orderItems.add(
                    createInsertDtoOrderItem(newProduct, orderItemAmount)
            );
        }
        return orderItems;
    }

    public @Valid OrderDto<OrderDto.OrderItemSelectDto> createPersistedOrder(
            int itemAmount,
            @Validated(NullCheckGroup.OnRead.class) PlatformUserDto user,
            numberRange itemSelectionRange
    ) {
        OrderDto<OrderDto.OrderItemInsertDto> newOrder = createInsertDtoOrder(
                itemAmount, user, itemSelectionRange
        );
        return orderServiceImp.createOrder(newOrder);
    }

    @Value
    @Builder
    public static class numberRange {
        int minAmount;
        int maxAmount;
    }
}
