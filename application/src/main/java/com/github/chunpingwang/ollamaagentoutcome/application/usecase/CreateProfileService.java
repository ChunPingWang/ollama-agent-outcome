package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.UUID;

/**
 * 建立個人資料的應用層服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    /**
     * 執行新增個人資料的業務邏輯。
     */
    @Override
    public void execute(CreateProfileRequest request) {
        ProfileId id = new ProfileId(UUID.randomUUID().toString());
        Email email = new Email(request.email());
        DisplayName displayName = (request.displayName() != null && !request.displayName().isBlank()) 
                ? new DisplayName(request.displayName()) 
                : new DisplayName("");
        
        Profile profile = Profile.create(id, email, displayName);
        saveProfilePort.save(profile);
    }
}
