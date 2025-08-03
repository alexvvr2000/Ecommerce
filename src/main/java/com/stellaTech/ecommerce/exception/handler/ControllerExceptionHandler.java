package com.stellaTech.ecommerce.exception.handler;

import com.stellaTech.ecommerce.exception.handler.ErrorMessage.ErrorMessage;
import com.stellaTech.ecommerce.exception.handler.ErrorMessage.InvalidObjectErrorMessage;
import com.stellaTech.ecommerce.exception.instance.InvalidPasswordField;
import com.stellaTech.ecommerce.exception.instance.InvalidProductQuantity;
import com.stellaTech.ecommerce.exception.instance.RepeatedUserPassword;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    private ResponseEntity<ErrorMessage> buildGenericMessage(Exception exception, HttpStatus status) {
        ErrorMessage message = ErrorMessage.builder()
                .errorName(exception.getClass().getName())
                .date(LocalDate.now())
                .message(exception.getMessage())
                .build();
        ResponseEntity<ErrorMessage> responseEntity = new ResponseEntity<>(message, HttpStatus.NOT_ACCEPTABLE);
        log.error(responseEntity.toString());
        return responseEntity;
    }

    @ExceptionHandler(value = InvalidPasswordField.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ErrorMessage> invalidPasswordFieldException(
            InvalidPasswordField exception, WebRequest request
    ) {
        return buildGenericMessage(exception, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = InvalidProductQuantity.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ErrorMessage> invalidPasswordFieldException(
            InvalidProductQuantity exception, WebRequest request
    ) {
        return buildGenericMessage(exception, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = RepeatedUserPassword.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> repeatedUserPassword(
            RepeatedUserPassword exception, WebRequest request
    ) {
        return buildGenericMessage(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(
            ResourceNotFoundException exception, WebRequest request
    ) {
        return buildGenericMessage(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<InvalidObjectErrorMessage> notValidArgumentException(
            MethodArgumentNotValidException exception, WebRequest request
    ) {
        InvalidObjectErrorMessage.InvalidObjectErrorMessageBuilder message = InvalidObjectErrorMessage.builder()
                .errorName(exception.getClass().getName())
                .message("Many fields in the request were not valid")
                .date(LocalDate.now());
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            FieldError fieldErrorData = ((FieldError) error);
            InvalidObjectErrorMessage.InvalidFieldMessage<Object> fieldErrorEntry = InvalidObjectErrorMessage
                    .InvalidFieldMessage
                    .builder()
                    .fieldName(fieldErrorData.getField())
                    .Message(fieldErrorData.getDefaultMessage())
                    .rejectedValue(fieldErrorData.getRejectedValue())
                    .build();
            message.invalidField(fieldErrorEntry);
        }
        ResponseEntity<InvalidObjectErrorMessage> responseEntity = new ResponseEntity<>(
                message.build(), HttpStatus.NOT_ACCEPTABLE
        );
        log.error(responseEntity.toString());
        return responseEntity;
    }
}
