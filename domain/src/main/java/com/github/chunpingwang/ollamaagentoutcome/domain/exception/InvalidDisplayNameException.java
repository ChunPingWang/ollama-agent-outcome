package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * Exception thrown when an invalid display name is provided (e.g., too long).
 */
public class InvalidDisplayNameException extends IllegalArgumentException {

    public InvalidDisplayNameException(String message) {
        super(message);
    }
}
