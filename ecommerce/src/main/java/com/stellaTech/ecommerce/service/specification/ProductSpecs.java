package com.stellaTech.ecommerce.service.specification;

import com.stellaTech.ecommerce.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
    public static Specification<Product> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Product> activeProductById(Long id) {
        return isNotDeleted().and(
                (root, query, cb) -> cb.equal(root.get("id"), id)
        );
    }
}
