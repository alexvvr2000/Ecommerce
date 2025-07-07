package com.stellaTech.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "order", schema = "product_data")
public class PurchasedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "purchased_date", nullable = false)
    @CreationTimestamp
    private Date purchasedDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_product")
    private Product purchasedProduct;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user")
    private PlatformUser orderUser;

    @Column(name = "product_count", nullable = false)
    private int productCount = 1;

    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;

    public PurchasedProduct() {
    }

    public PurchasedProduct(boolean deleted, int productCount, PlatformUser orderUser, Product purchasedProduct, Date purchasedDate, Integer id) {
        this.deleted = deleted;
        this.productCount = productCount;
        this.orderUser = orderUser;
        this.purchasedProduct = purchasedProduct;
        this.purchasedDate = purchasedDate;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(Date purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public Product getPurchasedProduct() {
        return purchasedProduct;
    }

    public void setPurchasedProduct(Product purchasedProduct) {
        this.purchasedProduct = purchasedProduct;
    }

    public PlatformUser getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(PlatformUser orderUser) {
        this.orderUser = orderUser;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
