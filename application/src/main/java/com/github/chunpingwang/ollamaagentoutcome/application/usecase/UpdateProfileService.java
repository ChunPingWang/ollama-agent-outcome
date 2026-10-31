package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;

/**
 * 更新個人資料的使用者情境實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        var profile = loadProfilePort.findById(command.id())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + command.id()));

        profile.setEmail(new Email(command.email()));
        profile.setDisplayName(new DisplayName(command.displayName()));
        if (command.bio() != null && !command.bio().isBlank()) {
            profile.setBio(new Bio(command.bio()));
        } else {
            profile.setBio(null);
        }

        saveProfilePort.save(profile);
    }
}
