package com.stellaTech.ecommerce.service.generics;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

public interface OrderService extends LogicallyDeletableEntityService {
    OrderDto<OrderDto.OrderItemSelectDto> createOrder(
            @Validated(NullCheckGroup.OnInsert.class) OrderDto<OrderDto.OrderItemInsertDto> dto
    ) throws ResourceNotFoundException;

    Page<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders(@NonNull Pageable pageable);

    OrderDto<OrderDto.OrderItemSelectDto> getOrderDtoById(Long id) throws ResourceNotFoundException;
}
