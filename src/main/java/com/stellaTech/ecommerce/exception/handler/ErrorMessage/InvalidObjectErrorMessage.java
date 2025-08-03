package com.stellaTech.ecommerce.exception.handler.ErrorMessage;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class InvalidObjectErrorMessage extends ErrorMessage {
    @Singular
    List<InvalidFieldMessage<?>> invalidFields;

    @Value
    @Builder
    @EqualsAndHashCode
    public static class InvalidFieldMessage<T> {
        String fieldName;
        String message;
        T rejectedValue;
    }
}
