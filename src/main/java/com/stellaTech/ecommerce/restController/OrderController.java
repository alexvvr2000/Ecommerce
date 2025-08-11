package com.stellaTech.ecommerce.restController;

import com.stellaTech.ecommerce.service.dto.NullCheckGroup;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.order.OrderService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/orders")
    public Page<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders(Pageable pageable) {
        log.info("Obtained page of order");
        return orderService.getAllOrders(pageable);
    }

    @GetMapping("/orders/{orderId}")
    public OrderDto<OrderDto.OrderItemSelectDto> getOrderById(@NonNull @PathVariable Long orderId) {
        log.info("Obtained order number {}", orderId);
        return orderService.getOrderDtoById(orderId);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDto<OrderDto.OrderItemSelectDto>> createOrder(@NonNull @RequestBody @Validated(NullCheckGroup.OnInsert.class) OrderDto<OrderDto.OrderItemInsertDto> orderInsertDto) {
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrder = orderService.createOrder(orderInsertDto);
        log.info("Created order with id {}", persistedOrder.getId());
        return ResponseEntity.ok(persistedOrder);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long orderId) {
        orderService.logicallyDeleteOrder(orderId);
        log.info("Deleted order with id {}", orderId);
        return ResponseEntity.noContent().build();
    }
}
