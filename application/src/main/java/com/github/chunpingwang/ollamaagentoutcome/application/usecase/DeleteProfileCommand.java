package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

/**
 * Command object for deleting a specific profile.
 */
public record DeleteProfileCommand(UUID id) {
}
