package com.stellaTech.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@NoArgsConstructor(force = true)
@Entity
@Table(name = "order", schema = "product_data")
public class Order extends LogicallyDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", updatable = false, nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "platform_user_id", updatable = false, nullable = false)
    private PlatformUser platformUser;

    @Column(name = "purchased_date", updatable = false, nullable = false)
    @CreationTimestamp
    private Date purchasedDate;

    @Column(name = "product_count", nullable = false, updatable = false)
    private int productCount = 1;

    public Order(@NonNull Product product, @NonNull PlatformUser platformUser, int productCount) throws Exception {
        this.product = product;
        this.platformUser = platformUser;
        this.setProductCount(productCount);
    }

    public void setProductCount(@NonNull Integer productCount) throws Exception {
        if (productCount < 0) {
            throw new Exception("The product count for the order is not valid");
        }
        this.productCount = productCount;
    }
}
