package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 更新個人資料的 Use Case 實作。
 * 負責載入資料、驗證新姓名、更新並儲存。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    /**
     * 建構子。
     *
     * @param loadProfilePort 載入個人資料的 Port
     * @param saveProfilePort 儲存個人資料的 Port
     */
    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public Profile updateProfile(ProfileId id, String newName) {
        // 1. 載入現有資料
        Profile existingProfile = loadProfilePort.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found: " + id));

        // 2. 驗證新姓名（若為空白會拋出 IllegalArgumentException）
        // Profile.updateName 會驗證 newName 是否為 null 或 blank
        Profile updatedProfile = existingProfile.updateName(newName);

        // 3. 儲存更新後的資料
        saveProfilePort.save(updatedProfile.getName(), updatedProfile.getEmail().getValue());

        return updatedProfile;
    }
}
