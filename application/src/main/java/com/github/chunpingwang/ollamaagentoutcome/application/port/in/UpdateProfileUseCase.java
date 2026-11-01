package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for updating an existing profile.
 */
public interface UpdateProfileUseCase {
    /**
     * Executes the update profile use case.
     * @param command contains id, new name and email to update
     */
    void execute(UpdateProfileCommand command);

    /**
     * Command object containing input data for update.
     */
    record UpdateProfileCommand(ProfileId id, String name, String email) {}
}
