package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * Outbound port for finding a profile by its unique identifier.
 */
public interface FindProfileByIdPort {
    /**
     * Finds a profile by ID.
     * @param id the unique identifier of the profile
     * @return an Optional containing the profile if found, empty otherwise
     */
    Optional<Profile> findById(UUID id);
}
