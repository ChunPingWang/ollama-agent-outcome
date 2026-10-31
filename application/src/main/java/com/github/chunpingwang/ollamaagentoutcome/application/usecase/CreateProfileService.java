package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

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
    public ProfileId create(String name, String email) {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile profile = new Profile(id, name, new Email(email));
        Profile savedProfile = saveProfilePort.save(profile);
        return savedProfile.id();
    }
}
