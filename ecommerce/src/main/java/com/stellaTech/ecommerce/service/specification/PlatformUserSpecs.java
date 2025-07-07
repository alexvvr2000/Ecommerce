package com.stellaTech.ecommerce.service.specification;

import com.stellaTech.ecommerce.model.PlatformUser;
import org.springframework.data.jpa.domain.Specification;

public class PlatformUserSpecs {
    public static Specification<PlatformUser> hasNotBeenDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<PlatformUser> activeUserById(Long id) {
        return hasNotBeenDeleted().and(
                (root, query, cb) -> cb.equal(root.get("id"), id)
        );
    }
}
