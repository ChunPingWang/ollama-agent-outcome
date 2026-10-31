package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;

/**
 * Inbound port for getting an existing profile by ID.
 */
public interface GetProfileUseCase {
    
    /**
     * Finds a profile by its ID.
     *
     * @param id the unique identifier of the profile
     * @return an optional containing the profile if found, empty otherwise
     */
    Optional<Profile> findById(UUID id);
}
