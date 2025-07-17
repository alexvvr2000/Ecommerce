package com.stellaTech.ecommerce.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class OrderDto {
    private int platformUserId;
    @Singular
    private List<OrderItemDto> orderItems;

    @Builder
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemDto{
        @EqualsAndHashCode.Include
        private int productId;
        private int quantity;
    }
}
