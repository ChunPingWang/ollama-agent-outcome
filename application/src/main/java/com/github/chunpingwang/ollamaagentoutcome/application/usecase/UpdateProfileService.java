package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileCommand;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.FindProfileByIdPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final FindProfileByIdPort findProfileByIdPort;
    private final FindProfileByEmailPort findProfileByEmailPort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(FindProfileByIdPort findProfileByIdPort,
                                FindProfileByEmailPort findProfileByEmailPort,
                                SaveProfilePort saveProfilePort) {
        this.findProfileByIdPort = findProfileByIdPort;
        this.findProfileByEmailPort = findProfileByEmailPort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public Profile execute(UpdateProfileCommand command) {
        Profile existingProfile = findProfileByIdPort.findById(command.id())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + command.id().getValue()));

        Email newEmail = command.email();

        // 檢查新電子郵件是否與其他現有資料重複
        if (findProfileByEmailPort.findByEmail(newEmail).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + newEmail.getValue());
        }

        // 更新業務邏輯
        existingProfile.updateName(command.name());
        existingProfile.updateEmail(newEmail);

        // 儲存更新後的實體
        return saveProfilePort.save(existingProfile);
    }
}
