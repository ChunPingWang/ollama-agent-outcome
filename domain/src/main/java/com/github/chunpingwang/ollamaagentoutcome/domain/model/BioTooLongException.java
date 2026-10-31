package com.github.chunpingwang.ollamaagentoutcome.domain.model;

/**
 * Exception thrown when bio exceeds maximum length.
 */
public class BioTooLongException extends IllegalArgumentException {
    public BioTooLongException(String message) {
        super(message);
    }
}
