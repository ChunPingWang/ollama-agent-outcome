package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import java.util.UUID;

import com.github.chunpingwang.ollamaagentoutcome.domain.DisplayName;
import com.github.chunpingwang.ollamaagentoutcome.domain.DuplicateEmailException;
import com.github.chunpingwang.ollamaagentoutcome.domain.Email;
import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.UpdateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.ExistsByEmailPort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;

/**
 * 更新個人資料的使用案例實作。
 */
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadProfilePort loadProfilePort;
    private final SaveProfilePort saveProfilePort;
    private final ExistsByEmailPort existsByEmailPort;

    public UpdateProfileService(LoadProfilePort loadProfilePort, SaveProfilePort saveProfilePort, ExistsByEmailPort existsByEmailPort) {
        this.loadProfilePort = loadProfilePort;
        this.saveProfilePort = saveProfilePort;
        this.existsByEmailPort = existsByEmailPort;
    }

    @Override
    public void update(String id, String email, String displayName) {
        ProfileId profileId = new ProfileId(UUID.fromString(id));
        
        // 1. 檢查資料是否存在
        Profile existingProfile = loadProfilePort.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));

        Email validatedEmail = new Email(email);
        DisplayName validatedDisplayName = new DisplayName(displayName);

        // 2. 檢查新電子郵件的唯一性（若與原郵件不同，則必須是全新的）
        boolean alreadyExists = existsByEmailPort.exists(validatedEmail);
        if (alreadyExists && !existingProfile.getEmail().getValue().equals(validatedEmail.getValue())) {
            throw new DuplicateEmailException("email already exists");
        }

        // 3. 建立新的 Profile 物件並儲存（因 Domain Entity 為不可變設計，直接覆寫更新）
        Profile updatedProfile = new Profile(profileId, validatedEmail, validatedDisplayName);
        saveProfilePort.save(updatedProfile);
    }
}
