package com.stellaTech.ecommerce.model.OrderManagement;

import com.stellaTech.ecommerce.model.Product;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "order_items", schema = "product_data")
public class OrderItem {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @EqualsAndHashCode.Include
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
        this.quantity = quantity;
        calculateSubtotal();
    }

    private void calculateSubtotal() {
        this.subtotal = purchasedPrice.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
