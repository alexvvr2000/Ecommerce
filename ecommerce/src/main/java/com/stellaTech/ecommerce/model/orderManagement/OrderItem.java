package com.stellaTech.ecommerce.model.orderManagement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.model.productManagement.ProductPriceSnapshot;
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

    @Column(nullable = false, updatable = false)
    private int quantity;

    @Embedded
    private ProductPriceSnapshot productPriceSnapshot;

    @Column(nullable = false, updatable = false)
    private BigDecimal subtotal;

    public OrderItem(@NonNull Product product, int quantity) {
        this.product = product;
        this.productPriceSnapshot = new ProductPriceSnapshot(product.getPrice());
        this.setQuantity(quantity);
    }

    private void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    private BigDecimal calculateSubtotal() {
        return productPriceSnapshot.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
