package com.roadtrip.attraction.exception;


import com.roadtrip.attraction.dto.ResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseBean> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ResponseBean errorDetails = ResponseBean.error(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseBean> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ResponseBean errorDetails = ResponseBean.error(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ResponseBean> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        ResponseBean errorDetails = ResponseBean.error(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // Handle validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBean> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ResponseBean errorDetails = ResponseBean.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle type mismatch exceptions
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseBean> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error = String.format("%s should be of type %s",
                ex.getName(), ex.getRequiredType().getName());

        ResponseBean errorDetails = ResponseBean.error(
                HttpStatus.BAD_REQUEST.value(),
                error
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBean> handleGlobalException(Exception ex, WebRequest request) {
        ResponseBean errorDetails = ResponseBean.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}