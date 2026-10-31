package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a profile with the given ID is not found.
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(UUID id) {
        super("Profile not found with id: " + id);
    }
}
