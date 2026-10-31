package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.time.Instant;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.profile.exception.DuplicateEmailException;

/**
 * 建立個人資料的 Use Case 實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileCommand command) {
        if (command.email() != null && !command.email().isBlank()) {
            var existing = loadProfilePort.findByEmail(command.email());
            if (existing.isPresent()) {
                throw new DuplicateEmailException(command.email());
            }
        }

        Profile profile = new Profile(
            UUID.randomUUID(),
            Email.of(command.email()),
            DisplayName.of(command.displayName()),
            Bio.of(command.bio())
        );

        // The port returns the saved domain object with updated state (e.g., isNew=false)
        saveProfilePort.save(profile);
    }
}
