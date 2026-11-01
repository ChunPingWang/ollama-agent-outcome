package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when attempting to create or update with a duplicate email address.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
