package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * Service implementation for creating a new user profile.
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public CreateProfileService(SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        this.saveProfilePort = saveProfilePort;
        this.existsByEmailPort = existsByEmailPort;
    }

    @Override
    public void execute(ProfileId id, Email email, DisplayName displayName) {
        if (existsByEmailPort.existsByEmail(email.address(), null)) {
            throw new DuplicateEmailException(email.address());
        }
        
        Profile profile = Profile.create(id, email, displayName);
        saveProfilePort.save(profile);
    }
}
