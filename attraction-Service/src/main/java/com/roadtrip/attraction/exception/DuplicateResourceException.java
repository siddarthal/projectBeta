package com.roadtrip.attraction.exception;

// Exception for duplicate resource
public class DuplicateResourceException extends TravelAppException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
