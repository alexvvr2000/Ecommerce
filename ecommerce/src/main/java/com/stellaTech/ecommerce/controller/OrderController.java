package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.model.Order;
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
        return orderService.getAllActiveOrders();
    }

    @GetMapping("/orders/{orderId}")
    public Order getAllOrders(@NonNull @PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@NonNull @RequestBody OrderService.NewOrder newOrder) {
        Order persistedOrder = orderService.createOrder(newOrder);
        return ResponseEntity.ok(persistedOrder);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long orderId) {
        orderService.logicalDeleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
