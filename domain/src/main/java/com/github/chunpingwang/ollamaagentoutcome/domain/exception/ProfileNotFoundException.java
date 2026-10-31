package com.github.chunpingwang.ollamaagentoutcome.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a profile with the specified ID cannot be found.
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(UUID id) {
        super("Profile not found with id: " + id);
    }
    
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
