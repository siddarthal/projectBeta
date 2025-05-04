package com.roadtrip.attraction.exception;

public class TravelAppException extends RuntimeException {
    public TravelAppException(String message) {
        super(message);
    }

    public TravelAppException(String message, Throwable cause) {
        super(message, cause);
    }
}

