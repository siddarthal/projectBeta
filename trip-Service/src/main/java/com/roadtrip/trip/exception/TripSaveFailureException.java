package com.roadtrip.trip.exception;

public class TripSaveFailureException extends RuntimeException {
    public TripSaveFailureException(Throwable throwable) {
        super("Unable to save trip ", throwable);
    }
}
