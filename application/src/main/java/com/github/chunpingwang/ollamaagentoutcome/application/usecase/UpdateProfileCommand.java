package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * Command object for updating an existing profile's details.
 */
public record UpdateProfileCommand(UUID id, String email, String displayName) {
}
