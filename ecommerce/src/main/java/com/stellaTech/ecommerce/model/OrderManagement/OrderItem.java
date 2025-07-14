package com.stellaTech.ecommerce.model.OrderManagement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.stellaTech.ecommerce.model.ProductManagement.Product;
import com.stellaTech.ecommerce.model.ProductManagement.ProductPriceSnapshot;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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

    @Setter
    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    @JsonBackReference
    private Order order;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Setter
    @Column(nullable = false, updatable = false)
    private int quantity;

    @Embedded
    private ProductPriceSnapshot productPriceSnapshot;

    @Column(nullable = false, updatable = false)
    private BigDecimal subtotal;

    protected OrderItem() {
        this.productPriceSnapshot = new ProductPriceSnapshot(BigDecimal.ZERO);
    }

    public OrderItem(@NonNull Product product, int quantity) {
        this.product = product;
        this.productPriceSnapshot = new ProductPriceSnapshot(product.getPrice());
        this.quantity = quantity;
    }

    public void setProduct(@NonNull Product product) {
        this.product = product;
        this.productPriceSnapshot = new ProductPriceSnapshot(product.getPrice());
    }

    @PrePersist
    private void calculateSubtotal() {
        this.subtotal = productPriceSnapshot.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
