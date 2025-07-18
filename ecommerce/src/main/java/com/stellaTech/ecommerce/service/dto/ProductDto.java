package com.stellaTech.ecommerce.service.dto;

import com.stellaTech.ecommerce.service.dto.validationGroup.NonEmptyCheck;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

@Data
public class ProductDto {
    @Null(groups = Optional.class)
    @NotNull(groups = NonEmptyCheck.class)
    private Long id;

    @NotBlank(groups = NonEmptyCheck.class)
    private String name;

    @NotBlank(groups = NonEmptyCheck.class)
    private String mdFormatDescription;

    @NotBlank(groups = NonEmptyCheck.class)
    private String mainImageUrl;

    @Min(value = 0)
    private BigDecimal price;
}
