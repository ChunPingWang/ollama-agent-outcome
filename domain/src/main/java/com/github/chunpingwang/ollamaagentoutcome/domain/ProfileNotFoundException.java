package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * Domain exception thrown when a profile with the specified ID is not found.
 */
public class ProfileNotFoundException extends RuntimeException {

    private final UUID id;

    public ProfileNotFoundException(UUID id) {
        super("Profile not found with ID: " + id);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
