package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception thrown when a profile is not found by ID.
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String id) {
        super("Profile not found with id: " + id);
    }
}
