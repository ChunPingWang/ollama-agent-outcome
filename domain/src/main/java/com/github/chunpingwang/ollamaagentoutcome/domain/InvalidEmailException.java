package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when an email value object fails validation.
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
