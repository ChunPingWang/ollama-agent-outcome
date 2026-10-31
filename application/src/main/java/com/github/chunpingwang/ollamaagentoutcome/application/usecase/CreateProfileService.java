package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的使用案例實作類別。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(CreateProfileRequest request) {
        var email = new Email(request.email());
        var displayName = new DisplayName(request.displayName());
        var id = new ProfileId(UUID.randomUUID().toString());

        var profile = new Profile(id, email, displayName);
        saveProfilePort.save(profile);
    }
}
