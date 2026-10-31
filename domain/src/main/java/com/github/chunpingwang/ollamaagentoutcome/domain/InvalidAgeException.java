package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when age validation fails (e.g., negative value).
 */
public class InvalidAgeException extends RuntimeException {
    public InvalidAgeException() {
        super("Age must not be negative");
    }
}
