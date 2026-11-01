package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 新增個人資料的應用層服務實作。
 */
class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(String email, String displayName) {
        ProfileId id = new ProfileId(java.util.UUID.randomUUID().toString());
        Email emailVo = new Email(email);
        DisplayName nameVo = (displayName != null && !displayName.isBlank()) ? new DisplayName(displayName.trim()) : new DisplayName(null);
        
        Profile profile = Profile.create(id, emailVo, nameVo);
        saveProfilePort.save(profile);
    }
}
