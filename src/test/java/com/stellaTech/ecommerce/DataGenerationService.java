package com.stellaTech.ecommerce;

import com.github.javafaker.Faker;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DataGenerationService {
    protected final Faker faker = new Faker(new Locale("es-MX"));

    public @Validated(NullCheckGroup.OnInsert.class) PlatformUserDto createInsertUserDto() {
        return PlatformUserDto.builder()
                .curp(faker.regexify("[A-Z]{4}[0-9]{6}[A-Z]{6}[0-9]{2}"))
                .fullName(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phoneNumber(faker.phoneNumber().cellPhone())
                .password(faker.internet().password(8, 16))
                .build();
    }

    public @Validated(NullCheckGroup.OnInsert.class) ProductDto createInsertProductDto() {
        return ProductDto.builder()
                .name(faker.commerce().productName())
                .mdFormatDescription("**%.100s**".formatted(faker.lorem().paragraph()))
                .mainImageUrl(faker.internet().image())
                .price(new BigDecimal(faker.commerce().price().replace(",", ".")))
                .build();
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
            @Validated(NullCheckGroup.OnRead.class) List<ProductDto> productDtoList,
            numberRange itemAmountRange,
            @Validated(NullCheckGroup.OnRead.class) PlatformUserDto user
    ) {
        List<OrderDto.OrderItemInsertDto> orderItems = new ArrayList<>();
        for (ProductDto newProduct : productDtoList) {
            int orderItemAmount = faker.number().numberBetween(
                    itemAmountRange.minAmount, itemAmountRange.maxAmount
            );
            orderItems.add(
                    createInsertDtoOrderItem(newProduct, orderItemAmount)
            );
        }
        return OrderDto
                .<OrderDto.OrderItemInsertDto>builder()
                .platformUserId(user.getId())
                .orderItems(orderItems)
                .build();
    }

    @Value
    @Builder
    public static class numberRange {
        int minAmount;
        int maxAmount;
    }
}
