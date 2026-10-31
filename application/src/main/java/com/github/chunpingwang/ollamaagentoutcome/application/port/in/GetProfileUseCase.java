package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Inbound port for retrieving an existing profile by its ID.
 */
public interface GetProfileUseCase {

    /**
     * Finds a profile by its unique identifier.
     *
     * @param id the profile ID to look up
     * @return an Optional containing the profile if found, otherwise empty
     */
    Optional<Profile> getById(ProfileId id);
}
