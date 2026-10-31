package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.UUID;

/**
 * Inbound port for deleting a profile by ID.
 */
public interface DeleteProfileUseCase {
    
    /**
     * Deletes the profile identified by the given ID.
     *
     * @param id the unique identifier of the profile to delete
     */
    void execute(UUID id);
}
