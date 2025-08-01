package com.stellaTech.ecommerce.exception.handler.ErrorMessage;

import java.time.LocalDate;

public class InvalidObjectErrorMessage extends ErrorMessage {
    public InvalidObjectErrorMessage(String errorName, LocalDate date, String message) {
        super(errorName, date, message);
    }
}
