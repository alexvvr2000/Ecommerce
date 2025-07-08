package com.stellaTech.ecommerce.controller;

import com.stellaTech.ecommerce.exception.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.Order;
import com.stellaTech.ecommerce.service.OrderService;
import lombok.*;
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
    public ResponseEntity<?> createOrder(@NonNull @RequestBody OrderRequest newOrder) {
        try {
            Order persistedOrder = orderService.createOrder(newOrder.productId, newOrder.userId, newOrder.productCount);
            return ResponseEntity.ok(persistedOrder);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> logicalDeletePlatformUser(@NonNull @PathVariable Long orderId) {
        try {
            orderService.logicalDeleteOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Error("Internal server error"));
        }
    }

    @Value
    @AllArgsConstructor
    public static class OrderRequest{
            Long productId;
            Long userId;
            int productCount;
    }
}
