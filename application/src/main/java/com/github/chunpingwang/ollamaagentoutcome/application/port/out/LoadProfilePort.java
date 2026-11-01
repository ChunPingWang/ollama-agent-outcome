package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Optional;

/**
 * Outbound port for loading a profile by its unique identifier.
 */
public interface LoadProfilePort {
    /**
     * Finds a profile by its ID.
     * @param id the profile ID to look up
     * @return an optional containing the profile if found, empty otherwise
     */
    Optional<Profile> findById(ProfileId id);
}
