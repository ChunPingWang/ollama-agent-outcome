package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {
    /**
     * Executes the create profile use case.
     * @param command contains name and email to create
     * @return the ID of the newly created profile
     */
    ProfileId execute(CreateProfileCommand command);

    /**
     * Command object containing input data for creation.
     */
    record CreateProfileCommand(String name, String email) {}
}
