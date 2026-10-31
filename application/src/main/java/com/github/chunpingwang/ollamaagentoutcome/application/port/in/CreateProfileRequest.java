package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Request DTO for creating a new profile.
 */
public record CreateProfileRequest(String email, String displayName) {}
