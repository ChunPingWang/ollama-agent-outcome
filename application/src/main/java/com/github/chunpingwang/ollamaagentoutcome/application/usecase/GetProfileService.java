package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileId;

/**
 * 查詢個人資料的業務邏輯實作 (Use Case Implementation)。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public ProfileResult execute(ProfileId id) {
        return loadProfilePort.findById(id).map(profile -> 
            new ProfileResult(
                profile.email().value(),
                profile.displayName() != null ? profile.displayName().value() : null
            )
        ).orElseThrow(() -> new ProfileNotFoundException("找不到識別碼為 " + id.toString() + " 的個人資料"));
    }
}
