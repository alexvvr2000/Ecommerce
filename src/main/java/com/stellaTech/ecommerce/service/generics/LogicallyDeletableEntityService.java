package com.stellaTech.ecommerce.service.generics;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;

public interface LogicallyDeletableEntityService {
    void logicallyDeleteById(Long id) throws ResourceNotFoundException;
}
