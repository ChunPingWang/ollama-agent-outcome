package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.*;

import java.util.Optional;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public void execute(ProfileId id, Email email, DisplayName displayName) {
        // 1. 先載入現有資料，若不存在則拋出例外
        Profile existingProfile = loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id.id()));

        // 2. 更新領域實體狀態
        existingProfile.updateEmail(email);
        if (displayName != null) {
            existingProfile.updateDisplayName(displayName);
        }

        // 3. 儲存變更（內部會處理唯一性檢查）
        saveProfilePort.save(existingProfile);
    }
}
