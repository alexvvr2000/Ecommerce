package com.stellaTech.ecommerce.service.specification;

import com.stellaTech.ecommerce.model.Order;
import com.stellaTech.ecommerce.model.OrderPK;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecs {
    public static Specification<Order> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Order> hasId(OrderPK id) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("id").get("productId"), id.getProductId()),
                cb.equal(root.get("id").get("userId"), id.getUserId())
        );
    }
}
