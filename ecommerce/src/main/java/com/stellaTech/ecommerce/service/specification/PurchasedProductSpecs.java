package com.stellaTech.ecommerce.service.specification;

import com.stellaTech.ecommerce.model.PurchasedProduct;
import org.springframework.data.jpa.domain.Specification;

public class PurchasedProductSpecs {
    public static Specification<PurchasedProduct> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<PurchasedProduct> activeProductById(Long id) {
        return isNotDeleted().and(
                (root, query, cb) -> cb.equal(root.get("id"), id)
        );
    }
}
