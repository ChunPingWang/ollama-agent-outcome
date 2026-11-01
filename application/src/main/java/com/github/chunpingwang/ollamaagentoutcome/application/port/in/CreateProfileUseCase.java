package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * Inbound port for creating a new profile.
 */
public interface CreateProfileUseCase {

    /**
     * Execute the use case to create a profile with the given details.
     *
     * @param id        The unique identifier for the profile.
     * @param email     The email address of the user.
     * @param displayName The display name of the user.
     */
    void execute(ProfileId id, Email email, DisplayName displayName);
}
