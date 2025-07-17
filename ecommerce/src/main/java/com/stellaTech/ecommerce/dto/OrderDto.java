package com.stellaTech.ecommerce.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class OrderDto {
    private int platformUserId;
    private List<OrderItemDto> orderItems;

    @Data
    public static class OrderItemDto {
        @EqualsAndHashCode.Include
        private int productId;
        private int quantity;
    }
}
