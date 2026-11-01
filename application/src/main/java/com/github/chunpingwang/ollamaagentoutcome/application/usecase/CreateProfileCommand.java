package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

/**
 * Command object for creating a new profile.
 */
public record CreateProfileCommand(String email, String displayName) {
}
