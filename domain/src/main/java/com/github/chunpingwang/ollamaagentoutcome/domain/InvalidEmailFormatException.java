package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Exception thrown when an invalid email format is provided in business logic context.
 */
public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
