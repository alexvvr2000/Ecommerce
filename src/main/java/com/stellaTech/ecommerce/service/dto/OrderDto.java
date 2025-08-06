package com.stellaTech.ecommerce.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class OrderDto<T extends OrderDto.OrderItemDto> {
    @Null(groups = NullCheckGroup.OnInsert.class, message = "The id is handled automatically by the system")
    @NotNull(groups = NullCheckGroup.OnRead.class)
    Long id;

    @NotNull(message = "The platform user id must not be empty")
    Long platformUserId;

    @Null(groups = NullCheckGroup.OnInsert.class, message = "The price is handled by the system")
    @NotNull(groups = NullCheckGroup.OnRead.class)
    BigDecimal totalAmount;

    @NotEmpty(message = "An order must have 1 or more items")
    @Singular
    @NotNull
    List<T> orderItems;

    public interface OrderItemDto {
        Long getProductId();

        Integer getQuantity();

        BigDecimal getPrice();
    }

    @Value
    @Builder
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderItemInsertDto implements OrderItemDto {
        @NotNull
        @EqualsAndHashCode.Include
        Long productId;

        @Min(value = 1, message = "The item amount must be 1 or more")
        @NotNull
        Integer quantity;

        @Override
        public BigDecimal getPrice() throws RuntimeException {
            throw new RuntimeException("The price is set during runtime");
        }
    }

    @Value
    @Builder
    @EqualsAndHashCode
    public static class OrderItemSelectDto implements OrderItemDto {
        @NotNull
        Long orderItemId;

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
