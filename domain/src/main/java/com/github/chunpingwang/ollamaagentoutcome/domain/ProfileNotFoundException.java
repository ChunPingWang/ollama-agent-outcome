package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Exception thrown when a requested profile does not exist in the system.
 */
public class ProfileNotFoundException extends RuntimeException {

    private final String id;

    public ProfileNotFoundException(String id) {
        super("Profile not found with ID: " + id);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
