package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception for invalid email format or content.
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
