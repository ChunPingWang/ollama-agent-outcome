package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.service.ProfileNotFoundException;

/**
 * Service implementation for updating an existing profile's display name and bio.
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort);
    }

    @Override
    public void execute(UUID id, String displayName, String bio) {
        Profile existingProfile = loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));

        // Create value objects to trigger domain validation (e.g., Bio length limit)
        DisplayName newName = new DisplayName(displayName);
        Bio newBio = new Bio(bio);

        // Call domain method to update state
        existingProfile.updateDisplayNameAndBio(newName, newBio);

        // Persist the updated entity
        saveProfilePort.save(existingProfile);
    }
}
