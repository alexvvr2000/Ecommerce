package com.stellaTech.ecommerce.service.generics;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService extends LogicallyDeletableEntityService {
    OrderDto<OrderDto.OrderItemSelectDto> createOrder(
            OrderDto<OrderDto.OrderItemInsertDto> dto
    ) throws ResourceNotFoundException;

    Page<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders(Pageable pageable);

    OrderDto<OrderDto.OrderItemSelectDto> getOrderDtoById(Long id) throws ResourceNotFoundException;
}
