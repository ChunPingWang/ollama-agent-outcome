package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for deleting a profile by its ID.
 */
public interface DeleteProfileUseCase {

    /**
     * Deletes the profile associated with the given ID.
     *
     * @param id the ID of the profile to delete
     */
    void deleteById(ProfileId id);
}
