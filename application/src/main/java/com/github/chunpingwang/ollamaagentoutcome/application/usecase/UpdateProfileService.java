package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;

import java.util.Objects;
import java.util.Optional;

/**
 * 更新個人資料的使用案例實作。
 */
class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort) {
        this.loadProfilePort = Objects.requireNonNull(loadProfilePort);
        this.saveProfilePort = Objects.requireNonNull(saveProfilePort);
    }

    @Override
    public void execute(ProfileId id, Email email, DisplayName displayName) {
        Optional<Profile> existing = loadProfilePort.findById(id);
        if (existing.isEmpty()) {
            throw new ProfileNotFoundException(id.id());
        }

        Profile profile = existing.get();
        // 依領域模型支援之方法進行狀態變更（目前僅示範 updateDisplayName）
        profile.updateDisplayName(displayName);
        
        saveProfilePort.save(profile);
    }
}
