package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Request DTO for updating an existing profile.
 */
public record UpdateProfileRequest(String id, String email, String displayName) {}
