package com.stellaTech.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "order", schema = "product_data")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @EmbeddedId
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "productId", column = @Column(name = "product_id")),
                    @AttributeOverride(name = "userId", column = @Column(name = "user_id"))
            }
    )
    private OrderPK orderPK;
    @MapsId("productId")
    @ManyToOne
    private Product product;
    @MapsId("userId")
    @ManyToOne
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

    public Order(OrderPK orderPK, int productCount) throws Exception {
        this.setOrderPK(orderPK);
        this.setProductCount(productCount);
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

    public OrderPK getOrderPK() {
        return orderPK;
    }

    public void setOrderPK(OrderPK orderPK) {
        this.orderPK = orderPK;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
