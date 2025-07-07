package com.stellaTech.ecommerce.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderPK implements Serializable {
    private Long productId;
    private Long userId;

    public OrderPK() {
    }

    public OrderPK(Long productId, Long userId) {
        this.productId = productId;
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (this == anotherObject) return true;
        if (anotherObject == null || getClass() != anotherObject.getClass()) return false;
        OrderPK orderPK = (OrderPK) anotherObject;
        return Objects.equals(productId, orderPK.productId) &&
                Objects.equals(userId, orderPK.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, userId);
    }

}
