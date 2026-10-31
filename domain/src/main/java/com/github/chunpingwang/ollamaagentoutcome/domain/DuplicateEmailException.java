package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Exception thrown when a duplicate email address is detected during profile creation or update.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
