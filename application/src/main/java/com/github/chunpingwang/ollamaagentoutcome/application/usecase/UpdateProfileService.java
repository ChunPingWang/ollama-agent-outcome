package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.UUID;

class UpdateProfileService implements UpdateProfileUseCase {
    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        Profile existing = loadProfilePort.findById(new ProfileId(UUID.fromString(command.id())))
                .orElseThrow(() -> new ProfileNotFoundException("profile not found: " + command.id()));
        saveProfilePort.save(existing.update(new Name(command.name()), new Email(command.email())));
    }
}
