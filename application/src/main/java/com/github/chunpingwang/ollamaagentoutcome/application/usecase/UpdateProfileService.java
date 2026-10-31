package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import java.util.UUID;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void update(UUID id, String name, String email) {
        ProfileId profileId = new ProfileId(id);
        Profile existingProfile = loadProfilePort.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));
        
        Profile updatedProfile = new Profile(
                existingProfile.id(),
                name,
                new Email(email)
        );
        
        saveProfilePort.save(updatedProfile);
    }
}
