package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * Exception thrown when an invalid email format is provided.
 */
public class InvalidEmailException extends IllegalArgumentException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
