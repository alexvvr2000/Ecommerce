package com.stellaTech.ecommerce.exception.instance;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordField extends IllegalArgumentException {
    public InvalidPasswordField(String message) {
        super(message);
    }
}
