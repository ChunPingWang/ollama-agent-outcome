package com.github.chunpingwang.ollamaagentoutcome.domain;

import java.util.UUID;

/**
 * Domain exception thrown when a requested profile is not found by ID.
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(UUID id) {
        super("Profile not found for ID: " + id);
    }
}
