package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

import java.util.UUID;

class CreateProfileService implements CreateProfileUseCase {
    private final SaveProfilePort saveProfilePort;

    CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileCommand command) {
        Profile profile = Profile.create(
                new ProfileId(UUID.randomUUID()),
                new Name(command.name()),
                new Email(command.email())
        );
        saveProfilePort.save(profile);
    }
}
