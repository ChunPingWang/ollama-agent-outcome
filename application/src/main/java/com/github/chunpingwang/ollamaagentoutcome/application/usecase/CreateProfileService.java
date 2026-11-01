package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 建立個人資料的服務實作。
 */
class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public ProfileId execute(String email, String displayName) {
        Email emailValueObject = new Email(email);
        DisplayName displayNameValueObject = (displayName != null && !displayName.isBlank()) ? new DisplayName(displayName) : null;
        
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(id, emailValueObject, displayNameValueObject);
        
        try {
            saveProfilePort.save(profile);
        } catch (DuplicateEmailException e) {
            throw e; // 直接重拋，讓上層處理或記錄
        }
        
        return profile.id();
    }
}
