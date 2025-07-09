package com.stellaTech.ecommerce.model.OrderManagement;

import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.model.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Entity
@Table(name = "order_items", schema = "product_data")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem otherOrderItem)) return false;
        if (this.id != null && otherOrderItem.getId() != null) {
            return this.id.equals(otherOrderItem.getId());
        }
        return Objects.equals(order.getId(), otherOrderItem.getOrder().getId()) &&
                Objects.equals(product.getId(), otherOrderItem.getProduct().getId());
    }
}
