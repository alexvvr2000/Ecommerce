package com.stellaTech.ecommerce.restController;

import com.stellaTech.ecommerce.service.OrderService;
import com.stellaTech.ecommerce.service.dataDto.OrderDto;
import com.stellaTech.ecommerce.service.serviceDto.IdDtoResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/orders")
    public List<IdDtoResponse<OrderDto>> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{orderId}")
    public OrderDto getOrderById(@NonNull @PathVariable Long orderId) {
        return orderService.getOrderDtoById(orderId);
    }

    @PostMapping("/orders")
    public ResponseEntity<IdDtoResponse<OrderDto>> createOrder(@NonNull @RequestBody @Valid OrderDto orderInsertDto) {
        IdDtoResponse<OrderDto> persistedOrder = orderService.createOrder(orderInsertDto);
        return ResponseEntity.ok(persistedOrder);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long orderId) {
        orderService.logicallyDeleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
