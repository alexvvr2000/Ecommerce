package com.stellaTech.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "order", schema = "product_data",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_product_order_combination", columnNames = {"user_id", "product_id"})
        }
)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", updatable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private PlatformUser platformUser;

    @Column(name = "purchased_date", updatable = false)
    @CreationTimestamp
    private Date purchasedDate;

    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;

    @Column(name = "product_count", nullable = false, updatable = false)
    private int productCount = 1;

    public Order() {
    }

    public Order(Product product, PlatformUser platformUser, int productCount) {
        this.product = product;
        this.platformUser = platformUser;
        this.productCount = productCount;
    }

    public Long getId() {
        return id;
    }

    public PlatformUser getPlatformUser() {
        return platformUser;
    }

    public Product getProduct() {
        return product;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) throws Exception {
        if (productCount < 0) {
            throw new Exception("The product count for the order is not valid");
        }
        this.productCount = productCount;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
