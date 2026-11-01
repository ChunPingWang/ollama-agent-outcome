package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import java.util.Objects;
import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort);
    }

    @Override
    public void execute(UUID id, String email, String displayName) {
        Profile existing = loadProfilePort.findById(id)
            .orElseThrow(() -> new ProfileNotFoundException("profile not found: " + id));

        Profile updated = Profile.create(
            existing.id(),
            new Email(email),
            new DisplayName(displayName)
        );
        saveProfilePort.save(updated);
    }
}
