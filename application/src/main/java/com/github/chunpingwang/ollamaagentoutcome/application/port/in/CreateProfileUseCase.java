package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

/**
 * Inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {
    
    /**
     * Executes the create profile use case with the given command.
     *
     * @param email the email address (must be unique and valid)
     * @param displayName the display name
     * @param bio the biography content
     */
    void execute(String email, String displayName, String bio);
}
