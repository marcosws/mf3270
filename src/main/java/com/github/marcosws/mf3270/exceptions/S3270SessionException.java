package com.github.marcosws.mf3270.exceptions;


/**
 * Custom exception class for handling errors related to S3270 sessions.
 * This exception can be thrown when there are issues with connecting, sending commands,
 * or any other session-related problems in the S3270 terminal interaction.
 * It extends RuntimeException, allowing it to be thrown without being declared in method signatures.
 * The class provides constructors for creating exceptions with a message and an optional cause.	
 */
public class S3270SessionException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public S3270SessionException(String message) {
		super(message);
	}

	public S3270SessionException(String message, Throwable cause) {
		super(message, cause);
	}

}
