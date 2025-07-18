package com.stellaTech.ecommerce.service.dataDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDto {
    private Long platformUserId;
    @NotNull
    @NotEmpty
    @Singular
    private List<OrderItemDto> orderItems = new ArrayList<>();

    public void addItem(OrderItemDto orderInsertDto) {
        this.orderItems.add(orderInsertDto);
    }

    public interface OrderItemDto {
        Long getProductId();

        int getQuantity();

        BigDecimal getPrice();
    }

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderInsertDto implements OrderItemDto {
        @EqualsAndHashCode.Include
        private Long productId;
        @Min(value = 1)
        private int quantity;

        @Override
        public BigDecimal getPrice() throws RuntimeException {
            throw new RuntimeException("The price is set during runtime");
        }
    }

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class OrderSelectDto implements OrderItemDto {
        @EqualsAndHashCode.Include
        Long orderItemId;
        @EqualsAndHashCode.Include
        Long orderId;
        Long productId;
        int quantity;
        BigDecimal price;
        BigDecimal subtotal;
    }
}
