package com.stellaTech.ecommerce.exception.handler.ErrorMessage;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@Value
@NonFinal
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ErrorMessage implements Serializable {
    @EqualsAndHashCode.Include
    String errorName;
    LocalDate date;
    String message;

    @Builder
    public ErrorMessage(String errorName, LocalDate date, String message) {
        this.errorName = errorName;
        this.date = date;
        this.message = message;
    }
}
