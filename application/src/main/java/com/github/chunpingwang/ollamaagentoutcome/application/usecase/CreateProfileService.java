package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的應用服務實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(ExistsByEmailPort existsByEmailPort, SaveProfilePort saveProfilePort) {
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(Email email, DisplayName displayName) {
        if (existsByEmailPort.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
        
        Profile profile = new Profile(new ProfileId(UUID.randomUUID()), email, displayName);
        saveProfilePort.save(profile);
    }
}
