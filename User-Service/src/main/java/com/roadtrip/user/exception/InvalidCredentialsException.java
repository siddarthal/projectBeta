package com.roadtrip.user.exception;

public class InvalidCredentialsException extends UserServiceException {
    public InvalidCredentialsException(String email) {
        super("Invalid email or password");
    }
}
