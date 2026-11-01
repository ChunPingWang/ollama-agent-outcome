package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * Outbound port for loading a profile by ID.
 */
public interface LoadProfilePort {

    /**
     * Finds a profile by its ID.
     * @param id The profile ID.
     * @return An optional containing the profile if found.
     */
    Optional<Profile> findById(ProfileId id);
}
