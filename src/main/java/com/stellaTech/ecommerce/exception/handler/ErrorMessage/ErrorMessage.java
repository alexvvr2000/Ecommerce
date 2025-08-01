package com.stellaTech.ecommerce.exception.handler.ErrorMessage;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ErrorMessage implements Serializable {
    @EqualsAndHashCode.Include
    String errorName;
    LocalDate date;
    String message;
}
