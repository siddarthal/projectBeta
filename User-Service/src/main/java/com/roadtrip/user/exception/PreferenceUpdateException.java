package com.roadtrip.user.exception;

public class PreferenceUpdateException extends UserServiceException {
    public PreferenceUpdateException(String message) {
        super("Failed to update user preferences: " + message);
    }

    public PreferenceUpdateException(String message, Throwable cause) {
        super("Failed to update user preferences: " + message, cause);
    }
}
