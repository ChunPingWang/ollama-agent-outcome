package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import java.util.UUID;

/**
 * Outbound port for deleting a profile by its unique identifier.
 */
public interface DeleteProfilePort {
    /**
     * Deletes the profile with the given ID.
     * @param id the unique identifier of the profile to delete
     */
    void deleteById(UUID id);
}
