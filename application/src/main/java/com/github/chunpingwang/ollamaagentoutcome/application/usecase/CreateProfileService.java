package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;
import com.github.chunpingwang.ollamaagentoutcome.application.command.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ValidateEmailUniquePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.entity.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.exception.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Bio;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.valueobject.ProfileId;

/**
 * 建立個人資料的使用情境實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final ValidateEmailUniquePort validateEmailUniquePort;
    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(ValidateEmailUniquePort validateEmailUniquePort, SaveProfilePort saveProfilePort) {
        this.validateEmailUniquePort = validateEmailUniquePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(CreateProfileCommand command) {
        // 1. 驗證 Email 唯一性 (排除任何 ID，因為是新增)
        if (!validateEmailUniquePort.isUnique(command.email(), null)) {
            throw new DuplicateEmailException("Duplicate email: " + command.email());
        }

        // 2. 組裝 Domain Entity
        var id = new ProfileId(UUID.randomUUID());
        var email = new Email(command.email());
        var displayName = command.displayName() != null ? new DisplayName(command.displayName()) : null;
        var bio = new Bio(command.bio());

        var profile = new Profile(id, email, displayName, bio);

        // 3. 儲存
        saveProfilePort.save(profile);
    }
}
