package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when a display name validation fails.
 */
public class InvalidDisplayNameException extends RuntimeException {
    public InvalidDisplayNameException(String message) {
        super(message);
    }
}
