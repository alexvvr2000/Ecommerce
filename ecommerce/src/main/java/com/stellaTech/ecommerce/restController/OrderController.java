package com.stellaTech.ecommerce.restController;

import com.stellaTech.ecommerce.service.OrderService;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ValidationGroup;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/orders")
    public List<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{orderId}")
    public OrderDto<OrderDto.OrderItemSelectDto> getOrderById(@NonNull @PathVariable Long orderId) {
        return orderService.getOrderDtoById(orderId);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDto<OrderDto.OrderItemSelectDto>> createOrder(@NonNull @RequestBody @Validated(ValidationGroup.OnInsert.class) OrderDto<OrderDto.OrderItemInsertDto> orderInsertDto) {
        OrderDto<OrderDto.OrderItemSelectDto> persistedOrder = orderService.createOrder(orderInsertDto);
        return ResponseEntity.ok(persistedOrder);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long orderId) {
        orderService.logicallyDeleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
