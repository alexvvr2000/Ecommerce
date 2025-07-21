package com.stellaTech.ecommerce.model.orderManagement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.model.productManagement.ProductPriceSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "order_items", schema = "product_data")
public class CustomerOrderItem {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    @JsonBackReference
    private CustomerOrder customerOrder;

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

    public CustomerOrderItem(@NonNull Product product, int quantity) {
        this.setProduct(product);
        this.setQuantity(quantity);
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productPriceSnapshot = new ProductPriceSnapshot(product);
    }

    public void setQuantity(int quantity) throws RuntimeException {
        if (this.product == null) {
            throw new RuntimeException("Before setting the quantity you must set the product first");
        }
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    private BigDecimal calculateSubtotal() {
        return productPriceSnapshot.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
