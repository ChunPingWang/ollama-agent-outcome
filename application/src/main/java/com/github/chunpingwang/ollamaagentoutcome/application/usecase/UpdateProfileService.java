package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.Optional;

import com.github.chunpingwang.ollamaagentoutcome.application.command.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ValidateEmailUniquePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 更新個人資料的使用情境實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final ValidateEmailUniquePort validateEmailUniquePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort,
            ValidateEmailUniquePort validateEmailUniquePort,
            SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.validateEmailUniquePort = validateEmailUniquePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(UpdateProfileCommand command) {
        ProfileId id = new ProfileId(command.id());

        Optional<Profile> profileOpt = findProfileByIdPort.findById(id);

        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }

        Profile profile = profileOpt.get();

        // 檢查 Email 是否變更且重複
        String oldEmailStr = profile.getEmail().address();
        String newEmailStr = command.email();

        if (!oldEmailStr.equals(newEmailStr)) {
            if (!validateEmailUniquePort.isUnique(newEmailStr, id.value())) {
                throw new DuplicateEmailException("Duplicate email: " + newEmailStr);
            }
            profile.changeEmail(new Email(newEmailStr));
        }

        // 更新 DisplayName
        if (command.displayName() != null && !command.displayName().equals(profile.getDisplayName().name())) {
            var displayNameVO = command.displayName().isBlank() ? null : new DisplayName(command.displayName());
            try {
                profile.changeDisplayName(displayNameVO);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid display name", e);
            }
        }

        // 更新 Bio
        if (command.bio() == null || !command.bio().equals(profile.getBio().content())) {
             profile.changeBio(new Bio(command.bio()));
        }

        saveProfilePort.save(profile);
    }
}
