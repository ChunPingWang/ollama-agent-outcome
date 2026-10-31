package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * Inbound port for updating an existing profile's display name and bio.
 */
public interface UpdateProfileUseCase {
    
    /**
     * Executes the update profile use case with the given parameters.
     *
     * @param id the unique identifier of the profile to update
     * @param displayName the new display name (can be null or empty)
     * @param bio the new biography content (can be null or empty)
     */
    void execute(UUID id, String displayName, String bio);
}
