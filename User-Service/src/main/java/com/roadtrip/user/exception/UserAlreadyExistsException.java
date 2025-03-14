package com.roadtrip.user.exception;

public class UserAlreadyExistsException extends UserServiceException {
    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists");
    }
}
