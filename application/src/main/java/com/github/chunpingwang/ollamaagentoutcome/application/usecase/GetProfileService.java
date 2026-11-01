package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Service implementation for GetProfileUseCase.
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public GetProfileResult execute(GetProfileQuery query) {
        Optional<com.github.chunpingwang.ollamaagentoutcome.domain.Profile> profile = loadProfilePort.findById(query.id());
        
        if (profile.isEmpty()) {
            throw new ProfileNotFoundException(query.id());
        }
        
        var p = profile.get();
        return new GetProfileResult(p.name(), p.email());
    }
}
