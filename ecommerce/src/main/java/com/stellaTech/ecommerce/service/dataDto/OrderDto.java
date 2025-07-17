package com.stellaTech.ecommerce.service.dataDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class OrderDto {
    private int platformUserId;
    @NotNull
    @NotEmpty
    private List<OrderItemDto> orderItems;

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemDto {
        @EqualsAndHashCode.Include
        private int productId;
        @Min(value = 1)
        private int quantity;
    }
}
