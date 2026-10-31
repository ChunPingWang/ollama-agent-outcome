package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Objects;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Name;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;

/**
 * 更新個人資料的使用情境實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort);
        this.existsByEmailPort = Objects.requireNonNull(existsByEmailPort);
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        UUID idValue = command.id();
        if (idValue == null) {
            throw new IllegalArgumentException("profile id cannot be null");
        }

        Profile profile = loadProfilePort.findById(new ProfileId(idValue))
                .orElseThrow(() -> new ProfileNotFoundException(idValue));

        String oldEmail = profile.email().value();
        String newEmail = command.email();
        
        if (!oldEmail.equals(newEmail)) {
             if (existsByEmailPort.existsByEmail(newEmail)) {
                 throw new DuplicateEmailException();
             }
        }

        Name newName = new Name(command.name());
        Email newEmailVO = new Email(command.email());
        Bio newBio = new Bio(command.bio());

        profile.update(newName, newEmailVO, newBio);
        
        saveProfilePort.save(profile);
    }
}
