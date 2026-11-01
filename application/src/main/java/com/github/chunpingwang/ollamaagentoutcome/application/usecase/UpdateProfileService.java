package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for updating an existing user profile.
 * Validates existence, updates fields via domain entity method, and persists changes.
 */
class UpdateProfileService implements UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final SaveProfilePort saveProfilePort;

    /**
     * Constructs an UpdateProfileService with the given ports.
     *
     * @param findProfileByIdPort the port used to fetch profiles by ID
     * @param saveProfilePort     the port used to persist profile changes
     */
    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort, SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        UUID id = command.id();
        
        // Check if profile exists
        Optional<Profile> optionalProfile = findProfileByIdPort.findById(id);
        if (optionalProfile.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        
        Profile profile = optionalProfile.get();
        
        // Validate input data using Value Objects before updating
        Email newEmail = new Email(command.email());
        DisplayName newDisplayName = new DisplayName(command.displayName());
        
        // Update domain entity state
        profile.update(newEmail, newDisplayName);
        
        // Persist updated profile
        saveProfilePort.save(profile);
    }
}
