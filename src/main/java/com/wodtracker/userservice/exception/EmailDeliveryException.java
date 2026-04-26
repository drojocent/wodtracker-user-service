package com.wodtracker.userservice.exception;

public class EmailDeliveryException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2961513402869863040L;

	public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
