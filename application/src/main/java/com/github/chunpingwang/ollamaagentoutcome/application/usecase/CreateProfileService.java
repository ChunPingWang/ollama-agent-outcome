package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.model.ProfileId;

import java.util.UUID;

/**
 * Service implementation for creating a new profile.
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(String email, String displayName, String bio) {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Email emailValueObject = new Email(email);
        DisplayName displayNameValueObject = new DisplayName(displayName);
        Bio bioValueObject = new Bio(bio);

        Profile profile = new Profile(id, emailValueObject, displayNameValueObject, bioValueObject);
        
        // The port handles persistence and potential duplicate checks (translating DB exceptions to domain ones if needed)
        // However, based on the test structure, we just pass it through. 
        // If DuplicateEmailException is thrown by the port, it propagates up.
        saveProfilePort.save(profile);
    }
}
