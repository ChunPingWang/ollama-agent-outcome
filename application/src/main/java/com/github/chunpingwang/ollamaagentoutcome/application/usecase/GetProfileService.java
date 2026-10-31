package com.github.chunpingwang.ollamaagentoutcome.application.usecase;

import com.github.chunpingwang.ollamaagentoutcome.domain.Profile;
import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import com.github.chunpingwang.ollamaagentoutcome.application.port.in.GetProfileUseCase;
import com.github.chunpingwang.ollamaagentoutcome.application.port.out.LoadProfilePort;

/**
 * 查詢個人資料的 Use Case 實作。
 */
public class GetProfileService implements GetProfileUseCase {

    private final LoadProfilePort loadProfilePort;

    public GetProfileService(LoadProfilePort loadProfilePort) {
        this.loadProfilePort = loadProfilePort;
    }

    @Override
    public ProfileResponse get(String profileId) {
        Profile profile = loadProfilePort.findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));

        return new ProfileResponse(
            profile.id().value(),
            profile.email().value(),
            profile.name().value(),
            profile.bio().value()
        );
    }
}
