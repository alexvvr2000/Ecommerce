package com.stellaTech.ecommerce.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderSelectDto {
    @NotNull
    @EqualsAndHashCode.Include
    Long platformUserId;

    @NotEmpty
    @NotNull
    @EqualsAndHashCode.Include
    Set<OrderItemSelectDto> items;

    @NotNull
    BigDecimal totalPrice;

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemSelectDto {
        @EqualsAndHashCode.Include
        @NotNull
        Long productId;

        @Min(value = 1, message = "You have to buy at least 1 product")
        @NotNull
        Integer productCount;

        @NotNull
        BigDecimal price;
    }
}
