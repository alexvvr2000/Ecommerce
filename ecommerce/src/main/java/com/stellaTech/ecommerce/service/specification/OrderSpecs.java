package com.stellaTech.ecommerce.service.specification;

import com.stellaTech.ecommerce.model.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecs {
    public static Specification<Order> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Order> hasNotBeenDeleted(Long orderId) {
        return isNotDeleted().and(
                (root, query, cb) -> cb.equal(root.get("id"), orderId)
        );
    }

    public static Specification<Order> hasNotBeenDeleted(Long productId, Long platformUserId){
        return isNotDeleted().and(
                orderByProduct(productId).and(orderByPlatformUser(platformUserId))
        );
    }

    public static Specification <Order> orderByProduct(Long productId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("product").get("id"), productId
        );
    }

    public static Specification <Order> orderByPlatformUser(Long platformUserId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("platformUser").get("id"), platformUserId
        );
    }

}
