package com.stellaTech.ecommerce.exception.instance;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProductQuantity extends RuntimeException {
    public InvalidProductQuantity(String message) {
        super(message);
    }
}
