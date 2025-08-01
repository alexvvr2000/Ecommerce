package com.stellaTech.ecommerce.model.orderManagement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.stellaTech.ecommerce.exception.instance.InvalidProductQuantity;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.model.productManagement.ProductPriceSnapshot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Setter
    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    @JsonBackReference
    private CustomerOrder customerOrder;

    @NotNull
    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Integer quantity;

    @Embedded
    private ProductPriceSnapshot productPriceSnapshot;

    @Column(nullable = false, updatable = false)
    private BigDecimal subtotal;

    public CustomerOrderItem(@NonNull Product product, @NonNull Integer quantity) {
        this.setProduct(product);
        this.setQuantity(quantity);
    }

    public void setProduct(@NonNull Product product) {
        this.product = product;
        this.productPriceSnapshot = new ProductPriceSnapshot(product);
    }

    public void setQuantity(@NonNull Integer quantity) throws InvalidProductQuantity {
        if (this.product == null) {
            throw new InvalidProductQuantity("Before setting the quantity you must set the product first");
        }
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    private BigDecimal calculateSubtotal() {
        return productPriceSnapshot.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
