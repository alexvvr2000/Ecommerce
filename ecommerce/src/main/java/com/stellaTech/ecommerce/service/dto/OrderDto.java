package com.stellaTech.ecommerce.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDto<T extends OrderDto.OrderItemDto> {
    @Null(groups = ValidationGroup.OnInsert.class)
    @NotNull(groups = ValidationGroup.OnRead.class)
    private Long id;

    @NotNull
    private Long platformUserId;

    @Null(groups = ValidationGroup.OnInsert.class)
    @NotNull(groups = ValidationGroup.OnRead.class)
    private Long totalAmount;

    @NotEmpty
    @Singular
    @NotNull
    private List<T> orderItems = new ArrayList<>();

    public void addItem(T orderInsertDto) {
        this.orderItems.add(orderInsertDto);
    }

    public interface OrderItemDto {
        Long getProductId();

        Integer getQuantity();

        BigDecimal getPrice();
    }

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemInsertDto implements OrderItemDto {
        @NotNull
        @EqualsAndHashCode.Include
        private Long productId;

        @Min(value = 1)
        @NotNull
        private Integer quantity;

        @Override
        public BigDecimal getPrice() throws RuntimeException {
            throw new RuntimeException("The price is set during runtime");
        }
    }

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemSelectDto implements OrderItemDto {
        @EqualsAndHashCode.Include
        @NotNull
        Long orderItemId;

        @EqualsAndHashCode.Include
        @NotNull
        Long orderId;

        @NotNull
        Long productId;

        @NotNull
        Integer quantity;

        @NotNull
        BigDecimal price;

        @NotNull
        BigDecimal subtotal;
    }
}
