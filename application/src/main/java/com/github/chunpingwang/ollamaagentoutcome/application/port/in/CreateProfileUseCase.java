package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {

    /**
     * Executes the create profile use case.
     * @param email The email address string.
     * @param displayName The display name string (nullable).
     */
    void execute(String email, String displayName);
}
