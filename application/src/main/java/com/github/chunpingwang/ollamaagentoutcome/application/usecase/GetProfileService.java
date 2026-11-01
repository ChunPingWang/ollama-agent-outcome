package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * Service implementation for retrieving a user profile by ID.
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfileByIdPort loadProfileByIdPort;

    public GetProfileService(LoadProfileByIdPort loadProfileByIdPort) {
        this.loadProfileByIdPort = loadProfileByIdPort;
    }

    @Override
    public ProfileData execute(ProfileId id) {
        Optional<Profile> optionalProfile = loadProfileByIdPort.findById(id.id());
        
        if (optionalProfile.isEmpty()) {
            throw new ProfileNotFoundException(id.id());
        }
        
        Profile profile = optionalProfile.get();
        return new ProfileData(
                profile.id().id(), 
                profile.email().address(), 
                profile.displayName() != null ? profile.displayName().name() : null
        );
    }
}
