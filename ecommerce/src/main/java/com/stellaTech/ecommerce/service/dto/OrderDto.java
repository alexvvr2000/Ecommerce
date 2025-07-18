package com.stellaTech.ecommerce.service.dto;

import com.stellaTech.ecommerce.service.dto.validationGroup.NonEmptyCheck;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class OrderDto<T extends OrderDto.OrderItemDto> {
    @Null(groups = Optional.class)
    @NotNull(groups = NonEmptyCheck.class)
    private Long id;

    @NotNull
    private Long platformUserId;

    @NotEmpty
    @Singular
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
        @NotBlank
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
        @NotNull(groups = NonEmptyCheck.class)
        Long orderItemId;

        @EqualsAndHashCode.Include
        @NotNull(groups = NonEmptyCheck.class)
        Long orderId;

        @NotNull(groups = NonEmptyCheck.class)
        Long productId;

        @NotNull(groups = NonEmptyCheck.class)
        Integer quantity;

        @NotNull(groups = NonEmptyCheck.class)
        BigDecimal price;

        @NotNull(groups = NonEmptyCheck.class)
        BigDecimal subtotal;
    }
}
