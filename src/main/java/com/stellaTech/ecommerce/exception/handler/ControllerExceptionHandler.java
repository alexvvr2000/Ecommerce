package com.stellaTech.ecommerce.exception.handler;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                exception.getClass().getName(),
                LocalDate.now(),
                exception.getMessage()
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> notValidArgumentException(MethodArgumentNotValidException exception, WebRequest request) {
        List<String> listOfErrors = exception.getBindingResult().getAllErrors().stream().map(
                objectError -> ((FieldError) objectError).getField()
        ).toList();
        ErrorMessage message = new ErrorMessage(
                exception.getClass().getName(),
                LocalDate.now(),
                "Invalid fields: " + listOfErrors
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_ACCEPTABLE);
    }
}
