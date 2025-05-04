package com.roadtrip.attraction.exception;

// Exception for data validation failures
public class BadRequestException extends TravelAppException {
    public BadRequestException(String message) {
        super(message);
    }
}
