package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.UUID;

/**
 * Service implementation for creating a new user profile.
 * Validates input data and delegates persistence to SaveProfilePort.
 */
class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    /**
     * Constructs a CreateProfileService with the given port.
     *
     * @param saveProfilePort the port used to persist profiles
     */
    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileCommand command) {
        // Validate email format via Value Object constructor
        Email email = new Email(command.email());
        
        // Validate display name format via Value Object constructor
        DisplayName displayName = new DisplayName(command.displayName());
        
        // Generate ID (UUID v4 is standard for distributed systems without DB auto-gen)
        ProfileId id = new ProfileId(UUID.randomUUID());
        
        // Construct domain entity
        Profile profile = new Profile(id, email, displayName);
        
        // Persist using outbound port
        saveProfilePort.save(profile);
    }
}
