package com.stellaTech.ecommerce.model;

import com.stellaTech.ecommerce.exception.InvalidInputException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Embedded
    private ProductPriceSnapshot purchasedPrice;

    @Column(nullable = false)
    private BigDecimal subtotal;

    public OrderItem(@NonNull Product product, int quantity) {
        this.product = product;
        this.purchasedPrice = new ProductPriceSnapshot(product.getPrice());
        this.quantity = validateQuantity(quantity);
        calculateSubtotal();
    }

    private Integer validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be positive");
        }
        return quantity;
    }

    private void calculateSubtotal() {
        this.subtotal = purchasedPrice.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
