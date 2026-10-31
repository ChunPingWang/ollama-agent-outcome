package com.github.chunpingwang.ollamaagentoutcome.application.port.in;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * Application layer inbound port for getting a profile by ID.
 */
public interface GetProfileUseCase {
    Optional<Profile> findById(ProfileId id);
}
