package com.github.chunpingwang.ollamaagentoutcome.adapter.web;

import java.util.UUID;

/**
 * Response DTO representing a profile resource.
 */
public record ProfileResponse(UUID id, String email, String displayName) {
}
