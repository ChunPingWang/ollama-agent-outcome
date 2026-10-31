package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;

/**
 * Inbound port for retrieving a profile by ID.
 */
public interface GetProfileUseCase {
    Optional<Profile> execute(String id);
}
