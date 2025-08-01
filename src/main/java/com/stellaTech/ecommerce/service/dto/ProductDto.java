package com.stellaTech.ecommerce.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    @Null(groups = NullCheckGroup.OnInsert.class, message = "The id is handled by the system")
    @NotNull(groups = {NullCheckGroup.OnRead.class})
    private Long id;

    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    private String name;

    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    private String mdFormatDescription;

    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    private String mainImageUrl;

    @Min(value = 0, message = "The price must be 0 or positive")
    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    private BigDecimal price;
}
