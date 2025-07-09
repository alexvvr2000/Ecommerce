package com.stellaTech.ecommerce.service.dto;

import lombok.Value;

import java.util.Set;

@Value
public class OrderInsertDto {
    Long platformUserId;
    Set<OrderItemInsertDto> items;

    @Value
    public static class OrderItemInsertDto {
        Long productId;
        int productCount;
    }
}

