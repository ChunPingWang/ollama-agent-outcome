package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 建立個人資料的 Use Case 實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;
    private final FindProfileByEmailPort findProfileByEmailPort;

    public CreateProfileService(SaveProfilePort saveProfilePort, FindProfileByEmailPort findProfileByEmailPort) {
        this.saveProfilePort = saveProfilePort;
        this.findProfileByEmailPort = findProfileByEmailPort;
    }

    @Override
    public Profile execute(CreateProfileCommand command) {
        ProfileId profileId = command.id();
        String name = command.name();
        Email email = command.email();

        // 檢查電子郵件是否已存在
        if (findProfileByEmailPort.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + email.getValue());
        }

        // 建立 Profile 實體
        Profile profile = Profile.create(profileId, name, email);

        // 儲存並回傳結果
        return saveProfilePort.save(profile);
    }
}
