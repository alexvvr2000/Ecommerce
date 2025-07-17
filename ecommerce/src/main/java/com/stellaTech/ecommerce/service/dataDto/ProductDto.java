package com.stellaTech.ecommerce.service.dataDto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private String name;
    private String mdFormatDescription;
    private String mainImageUrl;
    @Min(value = 0)
    private BigDecimal price;
}
