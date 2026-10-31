package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

/**
 * Exception thrown when a duplicate email address is detected during persistence.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
