package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Exception thrown when a requested profile ID does not exist in the system.
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String id) {
        super("Profile with id " + id + " not found");
    }
}
