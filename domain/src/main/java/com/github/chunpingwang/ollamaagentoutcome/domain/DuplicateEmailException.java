package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Exception thrown when attempting to save a profile with an existing email address.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
