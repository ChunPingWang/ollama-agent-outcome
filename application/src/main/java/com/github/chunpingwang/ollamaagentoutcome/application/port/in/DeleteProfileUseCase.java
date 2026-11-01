package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for deleting a profile by ID.
 */
public interface DeleteProfileUseCase {

    /**
     * Execute the use case to delete the specified profile.
     *
     * @param id The unique identifier of the profile to delete.
     */
    void execute(ProfileId id);
}
