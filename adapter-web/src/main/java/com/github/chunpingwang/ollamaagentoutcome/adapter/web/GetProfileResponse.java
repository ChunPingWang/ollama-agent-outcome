package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.UUID;

/**
 * Response DTO for retrieving a profile.
 */
public record GetProfileResponse(UUID id, String name, String email) {}
