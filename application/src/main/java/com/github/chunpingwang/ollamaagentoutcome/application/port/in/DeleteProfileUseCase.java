package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for deleting a profile by ID.
 */
public interface DeleteProfileUseCase {
    /**
     * Executes the delete profile use case.
     * @param command contains the profile ID to remove
     */
    void execute(DeleteProfileCommand command);

    /**
     * Command object containing input data for deletion.
     */
    record DeleteProfileCommand(ProfileId id) {}
}
