package com.wodtracker.userservice.exception;

public class CannotDeleteCurrentUserException extends RuntimeException {

    public CannotDeleteCurrentUserException(String message) {
        super(message);
    }
}
