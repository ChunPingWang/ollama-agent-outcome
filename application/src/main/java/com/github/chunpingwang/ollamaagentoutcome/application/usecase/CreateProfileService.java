package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.*;

import java.util.UUID;

/**
 * 建立個人資料的 Use Case 實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public ProfileId execute(Email email, DisplayName displayName) {
        // Domain validation is handled by Email and DisplayName constructors
        Profile profile = new Profile(new ProfileId(UUID.randomUUID()), email, displayName);
        return saveProfilePort.save(profile).getId();
    }
}
