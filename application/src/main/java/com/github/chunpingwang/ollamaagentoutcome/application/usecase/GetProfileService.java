package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * Service implementation for GetProfileUseCase.
 */
public class GetProfileService implements GetProfileUseCase {
    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return loadProfilePort.loadById(id).orElseThrow(() -> new ProfileNotFoundException(id.value()));
    }
}
