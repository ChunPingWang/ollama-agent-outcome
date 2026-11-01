package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;

import java.util.Objects;
import java.util.UUID;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort, "loadProfilePort cannot be null");
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort, "saveProfilePort cannot be null");
    }

    @Override
    public void execute(UUID id, String email, String displayName) {
        Profile existingProfile = loadProfilePort.findById(new ProfileId(id))
                .orElseThrow(() -> new ProfileNotFoundException(id));

        try {
            existingProfile.setEmail(email);
            if (displayName != null && !displayName.isBlank()) {
                existingProfile.updateDisplayName(displayName);
            }
            saveProfilePort.save(existingProfile);
        } catch (DuplicateEmailException e) {
            throw e; // 直接傳遞領域例外，讓上層處理 HTTP 狀態碼轉換
        }
    }
}
