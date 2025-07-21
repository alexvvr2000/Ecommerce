package com.stellaTech.ecommerce.repository.specification;

import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecs {
    public static Specification<CustomerOrder> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<CustomerOrder> hasNotBeenDeleted(Long orderId) {
        return isNotDeleted().and(
                (root, query, cb) -> cb.equal(root.get("id"), orderId)
        );
    }

    public static Specification<CustomerOrder> orderByProduct(Long productId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("product").get("id"), productId
        );
    }

    public static Specification<CustomerOrder> orderByPlatformUser(Long platformUserId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("platformUser").get("id"), platformUserId
        );
    }

}
