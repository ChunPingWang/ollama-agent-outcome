package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * Exception thrown when a duplicate email constraint violation occurs.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
