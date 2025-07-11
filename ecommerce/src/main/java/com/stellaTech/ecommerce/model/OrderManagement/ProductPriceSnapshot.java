package com.stellaTech.ecommerce.model.OrderManagement;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
public class ProductPriceSnapshot {
    @Column(name = "purchased_price", nullable = false, precision = 8, scale = 2, updatable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "price_valid_at", nullable = false, updatable = false)
    private LocalDateTime validAt;

    public ProductPriceSnapshot(BigDecimal currentPrice) {
        this.price = currentPrice;
        this.validAt = LocalDateTime.now();
    }
}
