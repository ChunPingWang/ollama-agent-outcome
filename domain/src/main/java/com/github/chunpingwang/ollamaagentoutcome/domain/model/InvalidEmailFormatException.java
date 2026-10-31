package com.github.chunpingwang.ollamaagentoutcome.domain.model;

/**
 * Exception thrown for invalid email format.
 */
public class InvalidEmailFormatException extends IllegalArgumentException {
    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
