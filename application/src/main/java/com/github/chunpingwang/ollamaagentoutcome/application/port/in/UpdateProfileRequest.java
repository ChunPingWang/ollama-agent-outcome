package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Request object for updating an existing profile in the application layer.
 */
public record UpdateProfileRequest(String email, String displayName) {
}
