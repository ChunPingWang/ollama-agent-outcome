package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 建立個人資料的 Service 實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(ExistsByEmailPort existsByEmailPort, SaveProfilePort saveProfilePort) {
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public ProfileId execute(String email, String displayName) {
        Email emailValueObject = new Email(email);
        
        if (existsByEmailPort.existsByEmail(emailValueObject)) {
            throw new DuplicateEmailException("email already exists: " + email);
        }

        UUID id = UUID.randomUUID();
        Profile profile = Profile.create(id, email, displayName);
        Profile saved = saveProfilePort.save(profile);
        
        return saved.getId();
    }
}
