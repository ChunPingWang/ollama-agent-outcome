package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Request object for creating a new profile in the application layer.
 */
public record CreateProfileRequest(String email, String displayName) {
}
