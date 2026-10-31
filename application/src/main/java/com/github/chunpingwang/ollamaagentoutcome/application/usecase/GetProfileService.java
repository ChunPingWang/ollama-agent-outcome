package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileQuery;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.UUID;

class GetProfileService implements GetProfileUseCase {
    private final LoadProfilePort loadProfilePort;

    GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public Profile execute(GetProfileQuery query) {
        return loadProfilePort.findById(new ProfileId(UUID.fromString(query.id())))
                .orElseThrow(() -> new ProfileNotFoundException("profile not found: " + query.id()));
    }
}
