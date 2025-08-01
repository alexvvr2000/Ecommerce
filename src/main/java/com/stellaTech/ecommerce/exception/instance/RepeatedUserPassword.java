package com.stellaTech.ecommerce.exception.instance;

public class RepeatedUserPassword extends IllegalArgumentException {
    public RepeatedUserPassword(String message) {
        super(message);
    }
}
