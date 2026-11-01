package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 更新個人資料的應用服務實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(ProfileId id, DisplayName displayName) {
        var profileOpt = loadProfilePort.findById(id);
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        
        var existingProfile = profileOpt.get();
        // Create a new instance with updated display name to maintain immutability principle in domain model
        var updatedProfile = existingProfile.changeDisplayName(displayName);
        saveProfilePort.save(updatedProfile);
    }
}
