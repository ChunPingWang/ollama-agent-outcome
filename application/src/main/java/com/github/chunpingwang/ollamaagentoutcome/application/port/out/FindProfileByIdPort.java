package com.github.chunpingwang.ollamaagentoutcome.application.port.out;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for finding a profile by its unique ID.
 */
public interface FindProfileByIdPort {
    Optional<Profile> findById(UUID id);
}
