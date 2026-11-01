package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.CreateProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.SaveProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.*;

import java.util.UUID;

/**
 * 建立個人資料的使用案例實作。
 */
public class CreateProfileService implements CreateProfileUseCase {

    private final SaveProfilePort saveProfilePort;

    public CreateProfileService(SaveProfilePort saveProfilePort) {
        this.saveProfilePort = saveProfilePort;
    }

    @Override
    public ProfileId execute(Email email, DisplayName displayName) {
        // 1. 產生新的 UUID 作為識別碼
        ProfileId id = new ProfileId(UUID.randomUUID());
        
        // 2. 建立領域實體（建構子內會驗證 Email）
        Profile profile = new Profile(id, email, displayName);
        
        // 3. 呼叫 Port 儲存
        return saveProfilePort.save(profile).getId();
    }
}
