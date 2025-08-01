package com.stellaTech.ecommerce.repository.specification;

import com.stellaTech.ecommerce.model.productManagement.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
    public static Specification<Product> hasNotBeenDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Product> activeProductById(Long id) {
        return hasNotBeenDeleted().and(
                (root, query, cb) -> cb.equal(root.get("id"), id)
        );
    }
}
