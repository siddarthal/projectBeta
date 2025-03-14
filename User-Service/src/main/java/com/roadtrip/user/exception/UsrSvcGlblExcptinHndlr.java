package com.roadtrip.user.exception;

import com.roadtrip.user.dto.ResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class UsrSvcGlblExcptinHndlr {
    private static final Logger logger = LoggerFactory.getLogger(UsrSvcGlblExcptinHndlr.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseBean<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        logger.info("User not found exception {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(ResponseBean.error(
                HttpStatus.CONFLICT.value(), ex.getMessage()
        ));
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseBean<Void>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User already exists: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseBean.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseBean<Void>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        logger.error("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseBean.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler(PreferenceUpdateException.class)
    public ResponseEntity<ResponseBean<Void>> handlePreferenceUpdateException(PreferenceUpdateException ex) {
        logger.error("Preference update error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBean<Void>> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBean.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred. Please try again later."
                ));
    }
}
