package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import java.util.Optional;

/**
 * 更新個人資料的 Use Case 實作。
 */
class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final ExistsByEmailPort existsByEmailPort;
    private final SaveProfilePort saveProfilePort;

    UpdateProfileService(LoadProfilePort loadProfilePort, ExistsByEmailPort existsByEmailPort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.existsByEmailPort = existsByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        ProfileId profileId = new ProfileId(command.profileId());
        Optional<Profile> existingProfileOpt = loadProfilePort.findById(profileId);

        if (existingProfileOpt.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found: " + command.profileId());
        }

        Profile existingProfile = existingProfileOpt.get();
        Email newEmail = new Email(command.email());

        // 如果新 Email 與現有 Email 不同，則檢查是否重複
        if (!existingProfile.email().equals(newEmail)) {
            if (existsByEmailPort.existsByEmail(newEmail)) {
                throw new DuplicateEmailException("Email already exists: " + command.email());
            }
        }

        Profile updatedProfile = existingProfile.update(command.name(), newEmail);
        saveProfilePort.save(updatedProfile);
    }
}
