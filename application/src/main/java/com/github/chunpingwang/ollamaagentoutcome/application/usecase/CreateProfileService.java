package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.Objects;
import java.util.UUID;

/**
 * Service implementation for {@link CreateProfileUseCase}.
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort);
    }

    @Override
    public void execute(String email, String displayName) {
        Email emailVO = new Email(email);
        DisplayName nameVO = (displayName == null || displayName.isBlank()) ? new DisplayName("") : new DisplayName(displayName);
        
        Profile profile = new Profile(new ProfileId(UUID.randomUUID()), emailVO, nameVO);
        saveProfilePort.save(profile);
    }
}
