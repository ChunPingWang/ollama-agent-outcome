package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Exception thrown when a requested profile does not exist.
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String id) {
        super("profile not found: " + id);
    }
}
