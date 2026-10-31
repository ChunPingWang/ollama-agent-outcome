package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.InvalidProfileException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import java.util.UUID;

/**
 * 建立個人資料的 Use Case 實作。
 */
class CreateProfileService implements CreateProfileUseCase {

    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    CreateProfileService(ExistsByEmailPort existsByEmailPort, SaveProfilePort saveProfilePort) {
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileCommand command) {
        if (existsByEmailPort.existsByEmail(new Email(command.email()))) {
            throw new DuplicateEmailException("Email already exists: " + command.email());
        }

        Profile profile = new Profile(new ProfileId(UUID.randomUUID()), command.name(), new Email(command.email()));
        saveProfilePort.save(profile);
    }
}
