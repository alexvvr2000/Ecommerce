package com.stellaTech.ecommerce.repository.specification;

import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUserPassword;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

    public static Specification<PlatformUserPassword> activeUserPasswordById(Long platformUserId) {
        return (root, query, criteriaBuilder) -> {
            Join<PlatformUserPassword, PlatformUser> userJoin = root.join("platformUser", JoinType.INNER);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(userJoin.get("id"), platformUserId),
                    criteriaBuilder.equal(userJoin.get("deleted"), false)
            );
        };
    }
}
