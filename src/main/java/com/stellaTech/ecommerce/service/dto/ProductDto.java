package com.stellaTech.ecommerce.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    @Null(groups = ValidationGroup.OnInsert.class)
    @NotNull(groups = {ValidationGroup.OnRead.class})
    private Long id;

    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class, ValidationGroup.OnUpdate.class})
    private String name;

    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class, ValidationGroup.OnUpdate.class})
    private String mdFormatDescription;

    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class, ValidationGroup.OnUpdate.class})
    private String mainImageUrl;

    @Min(value = 0)
    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class, ValidationGroup.OnUpdate.class})
    private BigDecimal price;
}
