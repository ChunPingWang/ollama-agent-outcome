package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileDisplayNameUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 更新顯示名稱的服務實作。
 */
public class UpdateProfileDisplayNameService implements UpdateProfileDisplayNameUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileDisplayNameService(FindProfileByIdPort findProfileByIdPort, SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(ProfileId id, DisplayName newDisplayName) {
        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);
        
        Profile existingProfile = profileOpt.orElseThrow(() -> new ProfileNotFoundException(UUID.fromString(id.value())));
        
        // Domain logic: update display name
        existingProfile.updateDisplayName(newDisplayName);
        
        // Persist the change
        saveProfilePort.save(existingProfile);
    }
}
