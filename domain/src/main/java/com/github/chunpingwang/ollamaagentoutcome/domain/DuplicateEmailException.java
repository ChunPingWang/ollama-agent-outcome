package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when a duplicate email constraint is violated.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
