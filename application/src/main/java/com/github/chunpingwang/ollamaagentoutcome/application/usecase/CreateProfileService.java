package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 建立個人資料的使用案例實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public CreateProfileService(SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        this.saveProfilePort = saveProfilePort;
        this.existsByEmailPort = existsByEmailPort;
    }

    @Override
    public ProfileId create(String email, String displayName) {
        Email validatedEmail = new Email(email);
        
        if (existsByEmailPort.exists(validatedEmail)) {
            throw new DuplicateEmailException("email already exists: " + email);
        }

        UUID id = UUID.randomUUID();
        Profile profile = new Profile(new ProfileId(id), validatedEmail, new DisplayName(displayName));
        saveProfilePort.save(profile);
        
        return profile.getId();
    }
}
