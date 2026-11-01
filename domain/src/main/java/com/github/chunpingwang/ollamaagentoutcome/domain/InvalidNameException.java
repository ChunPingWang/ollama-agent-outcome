package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when a name value object fails validation.
 */
public class InvalidNameException extends RuntimeException {
    public InvalidNameException(String message) {
        super(message);
    }
}
