package com.stellaTech.ecommerce.exception.instance;

public class InvalidProductQuantity extends RuntimeException {
    public InvalidProductQuantity(String message) {
        super(message);
    }
}
