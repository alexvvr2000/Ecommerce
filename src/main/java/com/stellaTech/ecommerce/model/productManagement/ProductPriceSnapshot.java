package com.stellaTech.ecommerce.model.productManagement;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceSnapshot {
    @NotNull
    @Column(name = "purchased_price", nullable = false, precision = 8, scale = 2, updatable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "price_valid_at", nullable = false, updatable = false)
    private Date validAt;

    public ProductPriceSnapshot(Product product) {
        this.setPrice(product);
    }

    public void setPrice(Product product) {
        this.price = product.getPrice();
    }
}
