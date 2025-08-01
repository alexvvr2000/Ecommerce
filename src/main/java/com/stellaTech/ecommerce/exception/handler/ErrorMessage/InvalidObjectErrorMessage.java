package com.stellaTech.ecommerce.exception.handler.ErrorMessage;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class InvalidObjectErrorMessage extends ErrorMessage {
    @Singular
    List<InvalidFieldMessage<?>> invalidFields;

    @Builder
    public InvalidObjectErrorMessage(
            String errorName,
            LocalDate date,
            String message,
            @Singular List<InvalidFieldMessage<?>> invalidFields) {
        super(errorName, date, message);
        this.invalidFields = invalidFields;
    }

    @Value
    @Builder
    @EqualsAndHashCode
    public static class InvalidFieldMessage<T> {
        String fieldName;
        String Message;
        T rejectedValue;
    }
}
