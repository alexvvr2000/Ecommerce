package com.stellaTech.ecommerce.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Set;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderInsertDto {
    @NotNull
    @EqualsAndHashCode.Include
    Long platformUserId;

    @NotEmpty
    @NotNull
    @EqualsAndHashCode.Include
    Set<OrderItemInsertDto> items;

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemInsertDto {
        @EqualsAndHashCode.Include
        @NotNull
        Long productId;

        @Min(value = 1, message = "You have to buy at least 1 product")
        @NotNull
        Integer productCount;
    }
}

