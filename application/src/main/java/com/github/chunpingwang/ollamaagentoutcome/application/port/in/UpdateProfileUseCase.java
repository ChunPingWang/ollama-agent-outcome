package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for updating an existing profile.
 */
public interface UpdateProfileUseCase {

    /**
     * Updates the profile identified by {@code id} with new data.
     *
     * @param id      the ID of the profile to update
     * @param name    the updated name
     * @param email   the updated email address
     * @param age     the updated age
     */
    void update(ProfileId id, String name, String email, int age);
}
