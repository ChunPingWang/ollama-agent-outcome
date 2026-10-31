package com.github.chunpingwang.ollamaagentoutcome.domain.service;

import java.util.UUID;

/**
 * Exception thrown when a profile is not found by ID.
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(UUID id) {
        super("profile not found with id: " + id);
    }
    
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
