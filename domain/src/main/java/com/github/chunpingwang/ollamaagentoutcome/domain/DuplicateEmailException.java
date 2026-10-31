package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when an email address already exists in the system.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Email address already exists");
    }
}
