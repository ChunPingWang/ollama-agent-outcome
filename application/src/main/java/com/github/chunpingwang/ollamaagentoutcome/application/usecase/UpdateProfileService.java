package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 更新個人資料的應用層服務實作。
 */
class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String id, String email, String displayName) {
        Profile profile = loadProfilePort.findById(id).orElseThrow(() -> new ProfileNotFoundException("profile not found: " + id));
        
        Email updatedEmail = new Email(email);
        DisplayName updatedName = (displayName != null && !displayName.isBlank()) ? new DisplayName(displayName.trim()) : new DisplayName(null);
        
        profile.update(updatedEmail, updatedName);
        saveProfilePort.save(profile);
    }
}
