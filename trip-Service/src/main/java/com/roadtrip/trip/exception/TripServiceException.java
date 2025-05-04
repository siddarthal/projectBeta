package com.roadtrip.trip.exception;

public class TripServiceException extends RuntimeException {
    public TripServiceException(String message) {
        super(message);
    }

    public TripServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
