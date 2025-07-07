package com.stellaTech.ecommerce.service.specification;

import com.stellaTech.ecommerce.model.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecs {
    public static Specification<Order> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Order> activeProductById(Long id) {
        return isNotDeleted().and(
                (root, query, cb) -> cb.equal(root.get("id"), id)
        );
    }
}
