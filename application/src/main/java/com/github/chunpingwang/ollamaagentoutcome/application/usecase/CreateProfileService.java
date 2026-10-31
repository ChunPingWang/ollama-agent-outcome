package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileRequest;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidEmailFormatException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Service implementation for creating a new profile.
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileRequest request) {
        // Validate email format first to ensure domain consistency before creating entity
        try {
            new Email(request.email());
        } catch (InvalidEmailFormatException e) {
            throw new InvalidEmailFormatException("Invalid email format: " + request.email());
        }

        String idStr = UUID.randomUUID().toString();
        ProfileId id = new ProfileId(idStr);
        
        // Domain validation happens inside Email constructor, which throws InvalidEmailFormatException if invalid
        Profile profile = Profile.create(id, request.email(), request.displayName());
        
        saveProfilePort.save(profile);
    }
}
