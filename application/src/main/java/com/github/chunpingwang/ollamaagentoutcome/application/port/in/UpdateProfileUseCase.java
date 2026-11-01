package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;

/**
 * Inbound port for updating an existing profile.
 */
public interface UpdateProfileUseCase {

    /**
     * Execute the use case to update the profile with new details.
     *
     * @param id          The unique identifier of the profile to update.
     * @param email       The new email address.
     * @param displayName The new display name.
     */
    void execute(ProfileId id, Email email, DisplayName displayName);
}
