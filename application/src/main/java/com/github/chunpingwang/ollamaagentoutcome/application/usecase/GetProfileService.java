package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;

/**
 * Service implementation for retrieving a user profile by ID.
 */
public class GetProfileService implements GetProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;

    public GetProfileService(FindProfileByIdPort findProfileByIdPort) {
        this.findProfileByIdPort = findProfileByIdPort;
    }

    @Override
    public Profile execute(GetProfileQuery query) {
        return findProfileByIdPort.findById(query.id())
                .orElseThrow(() -> new ProfileNotFoundException(query.id()));
    }
}
