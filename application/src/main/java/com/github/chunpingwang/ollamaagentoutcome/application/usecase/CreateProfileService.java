package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 建立個人資料的使用情境實作。
 */
class CreateProfileService implements CreateProfileUseCase {

    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(ExistsByEmailPort existsByEmailPort, SaveProfilePort saveProfilePort) {
        this.existsByEmailPort = Objects.requireNonNull(existsByEmailPort);
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort);
    }

    @Override
    public void execute(CreateProfileCommand command) {
        if (existsByEmailPort.existsByEmail(command.email())) {
            throw new DuplicateEmailException();
        }

        Profile profile = new Profile(
                new ProfileId(UUID.randomUUID()),
                new Name(command.name()),
                new Email(command.email()),
                new Bio(command.bio())
        );

        saveProfilePort.save(profile);
    }
}
