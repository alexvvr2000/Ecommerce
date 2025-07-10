package com.stellaTech.ecommerce.dto.product;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
@EqualsAndHashCode
public class ProductInsertDto {
    @NotEmpty
    @NotNull
    String name;

    @Digits(integer = 6, fraction = 2, message = "The price must be 8 digits long; 6 for integers and 2 for decimals")
    @DecimalMin(value = "0.00", message = "The price must be greater than 0")
    @NotNull
    BigDecimal price;

    @NotEmpty
    String mdFormatDescription;

    @NotEmpty
    String mainImageUrl;

    @Digits(integer = 2, fraction = 2, message = "The rating must be 4 digits long; 2 for integers and 2 for decimals")
    @DecimalMin(value = "0.00", message = "The rating must be greater than 0")
    @DecimalMax(value = "10.00", message = "The rating can't be greater than 10")
    BigDecimal averageRating;
}
