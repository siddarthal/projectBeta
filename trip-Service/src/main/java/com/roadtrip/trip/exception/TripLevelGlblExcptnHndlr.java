package com.roadtrip.trip.exception;

import com.roadtrip.trip.dto.ResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class TripLevelGlblExcptnHndlr {
    private static final Logger log = LoggerFactory.getLogger(TripLevelGlblExcptnHndlr.class);

    @ExceptionHandler(TripSaveFailureException.class)
    public ResponseEntity<ResponseBean<Void>> failedSavingTrip(TripSaveFailureException e) {
        log.error("Exception occurred while saving trip {}", e.getMessage(), e.getCause());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<ResponseBean<Void>> unableToFindTrip(TripNotFoundException e) {
        log.error("Unable to fetch Trip , exception with : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(TripStopsUpdateFailureException.class)
    public ResponseEntity<ResponseBean<Void>> updateStopsFailure(TripStopsUpdateFailureException e) {
        log.error("Unable to update Trip , exception with : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseBean.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBean<Void>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBean.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred. Please try again later."
                ));
    }
}
