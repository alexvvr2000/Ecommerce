package com.stellaTech.ecommerce.exception.handler;

import com.stellaTech.ecommerce.exception.handler.ErrorMessage.ErrorMessage;
import com.stellaTech.ecommerce.exception.handler.ErrorMessage.InvalidObjectErrorMessage;
import com.stellaTech.ecommerce.exception.instance.InvalidPasswordField;
import com.stellaTech.ecommerce.exception.instance.InvalidProductQuantity;
import com.stellaTech.ecommerce.exception.instance.RepeatedUserPassword;
import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = InvalidPasswordField.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> invalidPasswordFieldException(
            InvalidPasswordField exception, WebRequest request
    ) {
        return buildGenericMessage(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidProductQuantity.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> invalidPasswordFieldException(
            InvalidProductQuantity exception, WebRequest request
    ) {
        return buildGenericMessage(exception, HttpStatus.BAD_REQUEST);
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<InvalidObjectErrorMessage> handleDtoValidationExceptions(
            MethodArgumentNotValidException exception
    ) {
        InvalidObjectErrorMessage.InvalidObjectErrorMessageBuilder<?, ?> message = InvalidObjectErrorMessage
                .builder()
                .errorName(exception.getClass().getName())
                .message("Many fields in the request were not valid")
                .date(LocalDate.now())
                .invalidFields(
                        buildInvalidDtoValidationFields(exception.getBindingResult())
                );
        ResponseEntity<InvalidObjectErrorMessage> responseEntity = new ResponseEntity<>(
                message.build(), HttpStatus.BAD_REQUEST
        );
        log.error(responseEntity.toString());
        return responseEntity;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<InvalidObjectErrorMessage> handleConstraintViolation(
            ConstraintViolationException ex) {

        InvalidObjectErrorMessage errorMessage = InvalidObjectErrorMessage.builder()
                .errorName(ex.getClass().getSimpleName())
                .message("Database validation failed")
                .date(LocalDate.now())
                .invalidFields(buildModelConstraintViolations(ex.getConstraintViolations()))
                .build();

        return ResponseEntity.badRequest().body(errorMessage);
    }

    private ResponseEntity<ErrorMessage> buildGenericMessage(
            Exception exception, HttpStatus status
    ) {
        ErrorMessage message = ErrorMessage.builder()
                .errorName(exception.getClass().getName())
                .date(LocalDate.now())
                .message(exception.getMessage())
                .build();
        ResponseEntity<ErrorMessage> responseEntity = new ResponseEntity<>(message, status);
        log.error(responseEntity.toString());
        return responseEntity;
    }

    private List<InvalidObjectErrorMessage.InvalidFieldMessage<?>> buildInvalidDtoValidationFields(
            BindingResult bindingResult
    ) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> InvalidObjectErrorMessage.InvalidFieldMessage.builder()
                        .fieldName(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<InvalidObjectErrorMessage.InvalidFieldMessage<?>> buildModelConstraintViolations(
            Set<ConstraintViolation<?>> violations
    ) {
        return violations.stream()
                .map(violation -> InvalidObjectErrorMessage
                        .InvalidFieldMessage.builder()
                        .fieldName(getPropertyPath(violation))
                        .message(violation.getMessage())
                        .rejectedValue(violation.getInvalidValue())
                        .build())
                .collect(Collectors.toList());
    }

    private String getPropertyPath(ConstraintViolation<?> violation) {
        return violation.getPropertyPath().toString();
    }
}
