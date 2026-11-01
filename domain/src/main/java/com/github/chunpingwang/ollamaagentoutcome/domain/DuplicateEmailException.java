package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when attempting to create or update a profile with a duplicate email address.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("email already exists");
    }
}
