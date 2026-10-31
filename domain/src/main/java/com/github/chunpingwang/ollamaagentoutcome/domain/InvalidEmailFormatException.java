package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when email format is invalid.
 */
public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("Invalid email format");
    }
}
