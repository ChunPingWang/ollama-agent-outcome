package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;

/**
 * Service implementation for getting an existing profile.
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return loadProfilePort.findById(id);
    }
}
