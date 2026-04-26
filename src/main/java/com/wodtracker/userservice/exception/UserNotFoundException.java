package com.wodtracker.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3686869360345731334L;

	public UserNotFoundException(String message) {
        super(message);
    }
}