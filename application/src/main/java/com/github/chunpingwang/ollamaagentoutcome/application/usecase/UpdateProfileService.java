package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 更新個人資料的 Use Case 實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    /**
     * 建構子注入。
     *
     * @param loadProfilePort 載入 Port
     * @param saveProfilePort 儲存 Port
     */
    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort, "loadProfilePort must not be null");
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort, "saveProfilePort must not be null");
    }

    @Override
    public void execute(UUID id, String email, String displayName) {
        ProfileId profileId = new ProfileId(id);
        
        Optional<Profile> existingOpt = loadProfilePort.findById(profileId);
        if (existingOpt.isEmpty()) {
            throw new ProfileNotFoundException(id);
        }
        
        Profile profile = existingOpt.get();
        profile.setEmail(new Email(email));
        profile.updateDisplayName(new DisplayName(displayName));
        
        try {
            saveProfilePort.save(profile);
        } catch (DuplicateEmailException e) {
            throw e; // 直接傳遞領域例外，讓上層處理 HTTP 狀態碼轉換
        }
    }
}
