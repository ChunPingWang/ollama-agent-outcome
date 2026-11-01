package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * Inbound port for getting a profile by ID.
 */
public interface GetProfileUseCase {

    /**
     * Executes the get profile use case.
     * @param id The profile ID.
     * @return An optional containing the profile if found.
     */
    Optional<Profile> execute(ProfileId id);
}
