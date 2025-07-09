package com.stellaTech.ecommerce.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Set;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderInsertDto {
    @EqualsAndHashCode.Include
    Long platformUserId;

    @EqualsAndHashCode.Include
    Set<OrderItemInsertDto> items;

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemInsertDto {
        @EqualsAndHashCode.Include
        Long productId;

        int productCount;
    }
}

