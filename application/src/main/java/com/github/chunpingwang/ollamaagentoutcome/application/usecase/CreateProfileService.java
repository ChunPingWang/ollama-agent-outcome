package com.github.chunpingwang.ollamaagentoutcome.application.usecase;


import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;

import java.util.Objects;
import java.util.UUID;

/**
 * 建立個人資料的 Use Case 實作。
 */
class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort, "saveProfilePort cannot be null");
    }

    @Override
    public void execute(UUID id, String email, String displayName) {
        Profile profile = new Profile(new ProfileId(id), new Email(email), new DisplayName(displayName));
        try {
            saveProfilePort.save(profile);
        } catch (DuplicateEmailException e) {
            throw e; // 直接傳遞領域例外，讓上層處理 HTTP 狀態碼轉換
        }
    }
}
