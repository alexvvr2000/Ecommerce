package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.dto.order.OrderInsertDto;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.service.OrderService;
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
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{orderId}")
    public Order getAllOrders(@NonNull @PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@NonNull @RequestBody OrderInsertDto orderInsertDto) {
        Order persistedOrder = orderService.createOrder(orderInsertDto);
        return ResponseEntity.ok(persistedOrder);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long orderId) {
        orderService.logicallyDeleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
