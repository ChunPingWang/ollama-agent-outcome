package com.github.chunpingwang.ollamaagentoutcome.domain;

/**
 * Domain exception indicating that the requested profile was not found.
 */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String id) {
        super("Profile with id " + id + " not found");
    }
}
