package com.stellaTech.ecommerce.dto.product;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
@EqualsAndHashCode
public class ProductUpdateDto {
    @NotNull
    @NotEmpty
    String name;

    @NotNull
    @Digits(integer = 6, fraction = 2, message = "The price must be 8 digits long; 6 for integers and 2 for decimals")
    @DecimalMin(value = "0.00", message = "The price must be greater than 0")
    BigDecimal price;

    @NotNull
    @NotEmpty
    String mdFormatDescription;

    @NotNull
    @NotEmpty
    String mainImageUrl;

    @NotNull
    @Digits(integer = 2, fraction = 2, message = "The rating must be 4 digits long; 2 for integers and 2 for decimals")
    @DecimalMin(value = "0.00", message = "The rating must be greater than 0")
    @DecimalMax(value = "10.00", message = "The rating can't be greater than 10")
    BigDecimal averageRating;
}
